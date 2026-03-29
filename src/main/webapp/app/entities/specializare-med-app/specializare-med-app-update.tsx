import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getMedics } from 'app/entities/medic-med-app/medic-med-app.reducer';
import { createEntity, getEntity, reset, updateEntity } from './specializare-med-app.reducer';

export const SpecializareMedAppUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const medics = useAppSelector(state => state.medic.entities);
  const specializareEntity = useAppSelector(state => state.specializare.entity);
  const loading = useAppSelector(state => state.specializare.loading);
  const updating = useAppSelector(state => state.specializare.updating);
  const updateSuccess = useAppSelector(state => state.specializare.updateSuccess);

  const handleClose = () => {
    navigate(`/specializare-med-app${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

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
      ...specializareEntity,
      ...values,
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
          ...specializareEntity,
          medicis: specializareEntity?.medicis?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="medicalsystemApp.specializare.home.createOrEditLabel" data-cy="SpecializareCreateUpdateHeading">
            <Translate contentKey="medicalsystemApp.specializare.home.createOrEditLabel">Create or edit a Specializare</Translate>
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
                  id="specializare-med-app-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('medicalsystemApp.specializare.nume')}
                id="specializare-med-app-nume"
                name="nume"
                data-cy="nume"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('medicalsystemApp.specializare.medici')}
                id="specializare-med-app-medici"
                data-cy="medici"
                type="select"
                multiple
                name="medicis"
              >
                <option value="" key="0" />
                {medics
                  ? medics.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.gradProfesional ? `${otherEntity.gradProfesional}` : `Medicul #${otherEntity.id}`}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/specializare-med-app" replace color="info">
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

export default SpecializareMedAppUpdate;
