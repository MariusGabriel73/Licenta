import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getLocaties } from 'app/entities/locatie-med-app/locatie-med-app.reducer';
import { getEntities as getMedics } from 'app/entities/medic-med-app/medic-med-app.reducer';
import { createEntity, getEntity, reset, updateEntity } from './clinica-med-app.reducer';

export const ClinicaMedAppUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const locaties = useAppSelector(state => state.locatie.entities);
  const medics = useAppSelector(state => state.medic.entities);
  const clinicaEntity = useAppSelector(state => state.clinica.entity);
  const loading = useAppSelector(state => state.clinica.loading);
  const updating = useAppSelector(state => state.clinica.updating);
  const updateSuccess = useAppSelector(state => state.clinica.updateSuccess);

  const handleClose = () => {
    navigate(`/clinica-med-app${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getLocaties({}));
    dispatch(getMedics({}));
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

    const entity = {
      ...clinicaEntity,
      ...values,
      locatie: locaties.find(it => it.id.toString() === values.locatie?.toString()),
      medicis: mapIdList(values.medicis),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...clinicaEntity,
          locatie: clinicaEntity?.locatie?.id,
          medicis: clinicaEntity?.medicis?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="medicalsystemApp.clinica.home.createOrEditLabel" data-cy="ClinicaCreateUpdateHeading">
            <Translate contentKey="medicalsystemApp.clinica.home.createOrEditLabel">Create or edit a Clinica</Translate>
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
                  id="clinica-med-app-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('medicalsystemApp.clinica.nume')}
                id="clinica-med-app-nume"
                name="nume"
                data-cy="nume"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('medicalsystemApp.clinica.telefon')}
                id="clinica-med-app-telefon"
                name="telefon"
                data-cy="telefon"
                type="text"
              />
              <ValidatedField
                label={translate('medicalsystemApp.clinica.email')}
                id="clinica-med-app-email"
                name="email"
                data-cy="email"
                type="text"
              />
              <ValidatedField
                id="clinica-med-app-locatie"
                name="locatie"
                data-cy="locatie"
                label={translate('medicalsystemApp.clinica.locatie')}
                type="select"
              >
                <option value="" key="0" />
                {locaties
                  ? locaties.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.oras ? `${otherEntity.oras} — ` : ''}
                        {otherEntity.adresa ?? `Locatia #${otherEntity.id}`}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                label={translate('medicalsystemApp.clinica.medici')}
                id="clinica-med-app-medici"
                data-cy="medici"
                type="select"
                multiple
                name="medicis"
              >
                <option value="" key="0" />
                {medics
                  ? medics.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.user
                          ? `Dr. ${otherEntity.user.lastName} ${otherEntity.user.firstName}`
                          : (otherEntity.gradProfesional ?? `Medic #${otherEntity.id}`)}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/clinica-med-app" replace color="info">
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

export default ClinicaMedAppUpdate;
