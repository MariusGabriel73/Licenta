import React, { useEffect, useState, useCallback } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row, Label, Input, Spinner, Alert } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import axios from 'axios';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getPacients } from 'app/entities/pacient-med-app/pacient-med-app.reducer';
import { getEntities as getSecties } from 'app/entities/sectie-med-app/sectie-med-app.reducer';

import { IClinicaMedApp } from 'app/shared/model/clinica-med-app.model';
import { IMedicMedApp } from 'app/shared/model/medic-med-app.model';
import { ProgramareStatus } from 'app/shared/model/enumerations/programare-status.model';
import { createEntity, getEntity, reset, updateEntity } from './programare-med-app.reducer';

export const ProgramareMedAppUpdate = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const account = useAppSelector(state => state.authentication.account);
  const isAdmin = !!account?.authorities?.includes('ROLE_ADMIN');

  const pacients = useAppSelector(state => state.pacient.entities);
  const secties = useAppSelector(state => state.sectie.entities);

  const programareEntity = useAppSelector(state => state.programare.entity);
  const loading = useAppSelector(state => state.programare.loading);
  const updating = useAppSelector(state => state.programare.updating);
  const updateSuccess = useAppSelector(state => state.programare.updateSuccess);
  const programareStatusValues = Object.keys(ProgramareStatus);

  // Cascade state
  const [selectedClinicaId, setSelectedClinicaId] = useState<string>('');
  const [selectedSectieId, setSelectedSectieId] = useState<string>('');

  // Server-filtered data
  const [allClinicas, setAllClinicas] = useState<IClinicaMedApp[]>([]);
  const [filteredMedics, setFilteredMedics] = useState<IMedicMedApp[]>([]);
  const [loadingClinicas, setLoadingClinicas] = useState(false);
  const [loadingMedics, setLoadingMedics] = useState(false);

  // Auto-detected patient ID for non-admin users
  const [autoPatientId, setAutoPatientId] = useState<number | undefined>(undefined);
  const [patientError, setPatientError] = useState<string | undefined>(undefined);

  const handleClose = () => {
    navigate(`/programare-med-app${location.search}`);
  };

  // Load all clinicas on mount
  useEffect(() => {
    (async () => {
      setLoadingClinicas(true);
      try {
        const { data } = await axios.get<IClinicaMedApp[]>('api/clinicas?size=500');
        setAllClinicas(data);
      } finally {
        setLoadingClinicas(false);
      }
    })();
  }, []);

  // Load sections, and (for admin) all patients
  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
    dispatch(getSecties({}));
    if (isAdmin) {
      dispatch(getPacients({}));
    }
  }, []);

  // Auto-detect current patient for non-admin
  useEffect(() => {
    if (!isAdmin && isNew) {
      axios
        .get<{ id: number }>('api/pacients/me')
        .then(r => setAutoPatientId(r.data.id))
        .catch(() => setPatientError('Nu există un profil de pacient asociat contului tău.'));
    }
  }, [isAdmin, isNew]);

  // Pre-populate cascade from existing entity when editing
  useEffect(() => {
    if (programareEntity && !isNew) {
      if (programareEntity.clinica?.id) {
        setSelectedClinicaId(programareEntity.clinica.id.toString());
      }
      if (programareEntity.medic?.sectie?.id) {
        setSelectedSectieId(programareEntity.medic.sectie.id.toString());
      }
    }
  }, [programareEntity, isNew]);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  // Fetch medics whenever Clinica or Sectie changes
  const fetchMedics = useCallback(async (clinicaId: string, sectieId: string) => {
    if (!clinicaId || !sectieId) {
      setFilteredMedics([]);
      return;
    }
    setLoadingMedics(true);
    try {
      const { data } = await axios.get<IMedicMedApp[]>(`api/medics?clinicaId=${clinicaId}&sectieId=${sectieId}&eagerload=true&size=200`);
      setFilteredMedics(data);
    } catch {
      setFilteredMedics([]);
    } finally {
      setLoadingMedics(false);
    }
  }, []);

  const handleClinicaChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const val = e.target.value;
    setSelectedClinicaId(val);
    setSelectedSectieId('');
    setFilteredMedics([]);
  };

  const handleSectieChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const val = e.target.value;
    setSelectedSectieId(val);
    fetchMedics(selectedClinicaId, val);
  };

  const saveEntity = (values: any) => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    values.dataProgramare = convertDateTimeToServer(values.dataProgramare);

    const pacientId = isAdmin ? values.pacient : autoPatientId;

    const entity = {
      ...programareEntity,
      ...values,
      pacient: isAdmin ? pacients.find(it => it.id.toString() === pacientId?.toString()) : { id: autoPatientId },
      medic: filteredMedics.find(it => it.id.toString() === values.medic?.toString()),
      clinica: allClinicas.find(it => it.id.toString() === selectedClinicaId),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          status: 'ACTIVA',
          dataProgramare: displayDefaultDateTime(),
        }
      : {
          status: 'ACTIVA',
          ...programareEntity,
          dataProgramare: convertDateTimeFromServer(programareEntity.dataProgramare),
          pacient: programareEntity?.pacient?.id,
          medic: programareEntity?.medic?.id,
          clinica: programareEntity?.clinica?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="medicalsystemApp.programare.home.createOrEditLabel" data-cy="ProgramareCreateUpdateHeading">
            Adaugă sau Editează o Programare
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="programare-med-app-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                  className="d-none"
                />
              ) : null}
              {/* Patient selection - admin sees dropdown, patients are auto-detected */}
              {isAdmin ? (
                <ValidatedField label="Pacient" id="programare-med-app-pacient" name="pacient" data-cy="pacient" type="select" required>
                  <option value="">Selectați Pacientul</option>
                  {pacients
                    ? pacients.map(p => (
                        <option value={p.id} key={p.id}>
                          {p.user ? `${p.user.lastName} ${p.user.firstName}` : `Pacient (ID: ${p.id})`}
                        </option>
                      ))
                    : null}
                </ValidatedField>
              ) : patientError ? (
                <Alert color="danger">{patientError}</Alert>
              ) : autoPatientId ? (
                <p className="text-muted">
                  <FontAwesomeIcon icon="user" /> Programare pentru profilul tău de pacient.
                </p>
              ) : (
                <p>
                  <Spinner size="sm" /> Se identifică profilul de pacient...
                </p>
              )}
              {/* CASCADE: Clinica → Sectie → Medic */}
              <div className="p-4 mb-4 border rounded bg-white shadow-sm mt-3">
                <h5 className="mb-4 text-primary font-weight-bold">Selectează Clinica, Secția și Medicul</h5>

                <Row>
                  <Col md="6">
                    <Label for="clinica-select" className="font-weight-bold">
                      1. Selectează Clinica {loadingClinicas && <Spinner size="sm" />}
                    </Label>
                    <ValidatedField
                      id="programare-med-app-clinica"
                      name="clinica"
                      data-cy="clinica"
                      type="select"
                      onChange={handleClinicaChange as any}
                      required
                    >
                      <option value="" key="0">
                        {loadingClinicas ? 'Se încarcă...' : 'Alegeți Clinica'}
                      </option>
                      {allClinicas.map(c => (
                        <option value={c.id} key={c.id}>
                          {c.nume}
                        </option>
                      ))}
                    </ValidatedField>
                  </Col>

                  <Col md="6">
                    <Label for="sectie-filter" className="font-weight-bold">
                      2. Selectează Secția
                    </Label>
                    <Input
                      type="select"
                      id="sectie-filter"
                      className="form-control mb-3"
                      value={selectedSectieId}
                      onChange={handleSectieChange}
                      disabled={!selectedClinicaId}
                    >
                      <option value="">{!selectedClinicaId ? '← Alege întâi clinica' : 'Alegeți Secția'}</option>
                      {secties.map(sec => (
                        <option key={sec.id} value={sec.id}>
                          {sec.nume}
                        </option>
                      ))}
                    </Input>
                  </Col>
                </Row>

                <Row>
                  <Col md="12">
                    <Label for="medic-select" className="font-weight-bold">
                      3. Selectează Medicul {loadingMedics && <Spinner size="sm" />}
                    </Label>
                    <ValidatedField
                      id="programare-med-app-medic"
                      name="medic"
                      data-cy="medic"
                      type="select"
                      disabled={!selectedSectieId || filteredMedics.length === 0}
                      required
                    >
                      <option value="" key="0">
                        {!selectedClinicaId
                          ? '← Alege întâi clinica'
                          : !selectedSectieId
                            ? '← Alege secția'
                            : loadingMedics
                              ? 'Se încarcă...'
                              : filteredMedics.length === 0
                                ? 'Niciun medic disponibil în această secție'
                                : 'Alegeți Medicul'}
                      </option>
                      {filteredMedics.map(m => (
                        <option value={m.id} key={m.id}>
                          {m.user ? `Dr. ${m.user.lastName} ${m.user.firstName}` : `Medic (ID: ${m.id})`}
                          {m.gradProfesional ? ` — ${m.gradProfesional}` : ''}
                        </option>
                      ))}
                    </ValidatedField>
                  </Col>
                </Row>
              </div>
              {/* END CASCADE */}
              <ValidatedField
                label={translate('medicalsystemApp.programare.dataProgramare')}
                id="programare-med-app-dataProgramare"
                name="dataProgramare"
                data-cy="dataProgramare"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                required
              />
              <ValidatedField
                label={translate('medicalsystemApp.programare.status')}
                id="programare-med-app-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {programareStatusValues.map(s => (
                  <option value={s} key={s}>
                    {translate(`medicalsystemApp.ProgramareStatus.${s}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('medicalsystemApp.programare.observatii')}
                id="programare-med-app-observatii"
                name="observatii"
                data-cy="observatii"
                type="text"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/programare-med-app" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button
                color="primary"
                id="save-entity"
                data-cy="entityCreateSaveButton"
                type="submit"
                disabled={updating || (!isAdmin && !autoPatientId)}
              >
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Salvare</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default ProgramareMedAppUpdate;
