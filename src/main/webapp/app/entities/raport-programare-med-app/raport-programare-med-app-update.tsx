import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getProgramares } from 'app/entities/programare-med-app/programare-med-app.reducer';
import { createEntity, getEntity, reset, updateEntity } from './raport-programare-med-app.reducer';

export const RaportProgramareMedAppUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const programares = useAppSelector(state => state.programare.entities);
  const raportProgramareEntity = useAppSelector(state => state.raportProgramare.entity);
  const loading = useAppSelector(state => state.raportProgramare.loading);
  const updating = useAppSelector(state => state.raportProgramare.updating);
  const updateSuccess = useAppSelector(state => state.raportProgramare.updateSuccess);

  const handleClose = () => {
    navigate(`/raport-programare-med-app${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getProgramares({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    values.oraProgramata = convertDateTimeToServer(values.oraProgramata);
    values.oraInceputConsultatie = convertDateTimeToServer(values.oraInceputConsultatie);
    if (values.durataConsultatie !== undefined && typeof values.durataConsultatie !== 'number') {
      values.durataConsultatie = Number(values.durataConsultatie);
    }

    const entity = {
      ...raportProgramareEntity,
      ...values,
      programare: programares.find(it => it.id.toString() === values.programare?.toString()),
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
          oraProgramata: displayDefaultDateTime(),
          oraInceputConsultatie: displayDefaultDateTime(),
        }
      : {
          ...raportProgramareEntity,
          oraProgramata: convertDateTimeFromServer(raportProgramareEntity.oraProgramata),
          oraInceputConsultatie: convertDateTimeFromServer(raportProgramareEntity.oraInceputConsultatie),
          programare: raportProgramareEntity?.programare?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="medicalsystemApp.raportProgramare.home.createOrEditLabel" data-cy="RaportProgramareCreateUpdateHeading">
            <Translate contentKey="medicalsystemApp.raportProgramare.home.createOrEditLabel">Create or edit a RaportProgramare</Translate>
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
                  id="raport-programare-med-app-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('medicalsystemApp.raportProgramare.oraProgramata')}
                id="raport-programare-med-app-oraProgramata"
                name="oraProgramata"
                data-cy="oraProgramata"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('medicalsystemApp.raportProgramare.oraInceputConsultatie')}
                id="raport-programare-med-app-oraInceputConsultatie"
                name="oraInceputConsultatie"
                data-cy="oraInceputConsultatie"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('medicalsystemApp.raportProgramare.durataConsultatie')}
                id="raport-programare-med-app-durataConsultatie"
                name="durataConsultatie"
                data-cy="durataConsultatie"
                type="text"
              />
              <ValidatedField
                id="raport-programare-med-app-programare"
                name="programare"
                data-cy="programare"
                label={translate('medicalsystemApp.raportProgramare.programare')}
                type="select"
              >
                <option value="" key="0" />
                {programares
                  ? programares.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.dataProgramare
                          ? new Date(otherEntity.dataProgramare).toLocaleDateString('ro-RO')
                          : `Programare #${otherEntity.id}`}
                        {otherEntity.pacient?.user ? ` — ${otherEntity.pacient.user.lastName} ${otherEntity.pacient.user.firstName}` : ''}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/raport-programare-med-app" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default RaportProgramareMedAppUpdate;
