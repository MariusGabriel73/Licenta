import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getMedics } from 'app/entities/medic-med-app/medic-med-app.reducer';
import { getEntities as getLocaties } from 'app/entities/locatie-med-app/locatie-med-app.reducer';
import { createEntity, getEntity, reset, updateEntity } from './program-med-app.reducer';

export const ProgramMedAppUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const medics = useAppSelector(state => state.medic.entities);
  const locaties = useAppSelector(state => state.locatie.entities);
  const programEntity = useAppSelector(state => state.program.entity);
  const loading = useAppSelector(state => state.program.loading);
  const updating = useAppSelector(state => state.program.updating);
  const updateSuccess = useAppSelector(state => state.program.updateSuccess);

  const handleClose = () => {
    navigate(`/program-med-app${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getMedics({}));
    dispatch(getLocaties({}));
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
    values.oraStart = convertDateTimeToServer(values.oraStart);
    values.oraFinal = convertDateTimeToServer(values.oraFinal);

    const entity = {
      ...programEntity,
      ...values,
      medic: medics.find(it => it.id.toString() === values.medic?.toString()),
      locatie: locaties.find(it => it.id.toString() === values.locatie?.toString()),
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
          oraStart: displayDefaultDateTime(),
          oraFinal: displayDefaultDateTime(),
        }
      : {
          ...programEntity,
          oraStart: convertDateTimeFromServer(programEntity.oraStart),
          oraFinal: convertDateTimeFromServer(programEntity.oraFinal),
          medic: programEntity?.medic?.id,
          locatie: programEntity?.locatie?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="medicalsystemApp.program.home.createOrEditLabel" data-cy="ProgramCreateUpdateHeading">
            <Translate contentKey="medicalsystemApp.program.home.createOrEditLabel">Create or edit a Program</Translate>
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
                  id="program-med-app-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('medicalsystemApp.program.ziuaSaptamanii')}
                id="program-med-app-ziuaSaptamanii"
                name="ziuaSaptamanii"
                data-cy="ziuaSaptamanii"
                type="text"
              />
              <ValidatedField
                label={translate('medicalsystemApp.program.oraStart')}
                id="program-med-app-oraStart"
                name="oraStart"
                data-cy="oraStart"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('medicalsystemApp.program.oraFinal')}
                id="program-med-app-oraFinal"
                name="oraFinal"
                data-cy="oraFinal"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="program-med-app-medic"
                name="medic"
                data-cy="medic"
                label={translate('medicalsystemApp.program.medic')}
                type="select"
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
              <ValidatedField
                id="program-med-app-locatie"
                name="locatie"
                data-cy="locatie"
                label={translate('medicalsystemApp.program.locatie')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/program-med-app" replace color="info">
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

export default ProgramMedAppUpdate;
