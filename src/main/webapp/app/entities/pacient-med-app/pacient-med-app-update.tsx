import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { createEntity, getEntity, reset, updateEntity } from './pacient-med-app.reducer';

export const PacientMedAppUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const pacientEntity = useAppSelector(state => state.pacient.entity);
  const loading = useAppSelector(state => state.pacient.loading);
  const updating = useAppSelector(state => state.pacient.updating);
  const updateSuccess = useAppSelector(state => state.pacient.updateSuccess);

  const handleClose = () => {
    navigate(`/pacient-med-app${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
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
      ...pacientEntity,
      ...values,
      user: users.find(it => it.id.toString() === values.user?.toString()),
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
          ...pacientEntity,
          user: pacientEntity?.user?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="medicalsystemApp.pacient.home.createOrEditLabel" data-cy="PacientCreateUpdateHeading">
            <Translate contentKey="medicalsystemApp.pacient.home.createOrEditLabel">Create or edit a Pacient</Translate>
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
                  id="pacient-med-app-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('medicalsystemApp.pacient.cnp')}
                id="pacient-med-app-cnp"
                name="cnp"
                data-cy="cnp"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('medicalsystemApp.pacient.telefon')}
                id="pacient-med-app-telefon"
                name="telefon"
                data-cy="telefon"
                type="text"
              />
              <ValidatedField
                label={translate('medicalsystemApp.pacient.dataNasterii')}
                id="pacient-med-app-dataNasterii"
                name="dataNasterii"
                data-cy="dataNasterii"
                type="date"
              />
              <ValidatedField
                label={translate('medicalsystemApp.pacient.adresa')}
                id="pacient-med-app-adresa"
                name="adresa"
                data-cy="adresa"
                type="text"
              />
              <ValidatedField
                id="pacient-med-app-user"
                name="user"
                data-cy="user"
                label={translate('medicalsystemApp.pacient.user')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/pacient-med-app" replace color="info">
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

export default PacientMedAppUpdate;
