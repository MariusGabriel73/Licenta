import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { getEntities as getSecties } from 'app/entities/sectie-med-app/sectie-med-app.reducer';
import { getEntities as getClinicas } from 'app/entities/clinica-med-app/clinica-med-app.reducer';
import { getEntities as getSpecializares } from 'app/entities/specializare-med-app/specializare-med-app.reducer';
import { createEntity, getEntity, reset, updateEntity } from './medic-med-app.reducer';

export const MedicMedAppUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const secties = useAppSelector(state => state.sectie.entities);
  const clinicas = useAppSelector(state => state.clinica.entities);
  const specializares = useAppSelector(state => state.specializare.entities);
  const medicEntity = useAppSelector(state => state.medic.entity);
  const loading = useAppSelector(state => state.medic.loading);
  const updating = useAppSelector(state => state.medic.updating);
  const updateSuccess = useAppSelector(state => state.medic.updateSuccess);

  const handleClose = () => {
    navigate(`/medic-med-app${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
    dispatch(getSecties({}));
    dispatch(getClinicas({}));
    dispatch(getSpecializares({}));
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
      ...medicEntity,
      ...values,
      user: users.find(it => it.id.toString() === values.user?.toString()),
      sectie: secties.find(it => it.id.toString() === values.sectie?.toString()),
      clinicis: mapIdList(values.clinicis),
      specializaris: mapIdList(values.specializaris),
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
          ...medicEntity,
          user: medicEntity?.user?.id,
          sectie: medicEntity?.sectie?.id,
          clinicis: medicEntity?.clinicis?.map(e => e.id.toString()),
          specializaris: medicEntity?.specializaris?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="medicalsystemApp.medic.home.createOrEditLabel" data-cy="MedicCreateUpdateHeading">
            <Translate contentKey="medicalsystemApp.medic.home.createOrEditLabel">Create or edit a Medic</Translate>
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
                  id="medic-med-app-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('medicalsystemApp.medic.gradProfesional')}
                id="medic-med-app-gradProfesional"
                name="gradProfesional"
                data-cy="gradProfesional"
                type="text"
              />
              <ValidatedField
                label={translate('medicalsystemApp.medic.telefon')}
                id="medic-med-app-telefon"
                name="telefon"
                data-cy="telefon"
                type="text"
              />
              <ValidatedField
                label={translate('medicalsystemApp.medic.disponibil')}
                id="medic-med-app-disponibil"
                name="disponibil"
                data-cy="disponibil"
                check
                type="checkbox"
              />
              <ValidatedField
                id="medic-med-app-user"
                name="user"
                data-cy="user"
                label={translate('medicalsystemApp.medic.user')}
                type="select"
              >
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.lastName} {otherEntity.firstName} ({otherEntity.login})
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="medic-med-app-sectie"
                name="sectie"
                data-cy="sectie"
                label={translate('medicalsystemApp.medic.sectie')}
                type="select"
              >
                <option value="" key="0" />
                {secties
                  ? secties.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.nume}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                label={translate('medicalsystemApp.medic.clinici')}
                id="medic-med-app-clinici"
                data-cy="clinici"
                type="select"
                multiple
                name="clinicis"
              >
                <option value="" key="0" />
                {clinicas
                  ? clinicas.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.nume}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                label={translate('medicalsystemApp.medic.specializari')}
                id="medic-med-app-specializari"
                data-cy="specializari"
                type="select"
                multiple
                name="specializaris"
              >
                <option value="" key="0" />
                {specializares
                  ? specializares.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.nume}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/medic-med-app" replace color="info">
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

export default MedicMedAppUpdate;
