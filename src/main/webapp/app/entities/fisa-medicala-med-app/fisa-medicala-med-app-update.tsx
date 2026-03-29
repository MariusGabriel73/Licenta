import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getProgramares } from 'app/entities/programare-med-app/programare-med-app.reducer';
import { createEntity, getEntity, reset, updateEntity } from './fisa-medicala-med-app.reducer';

export const FisaMedicalaMedAppUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const programares = useAppSelector(state => state.programare.entities);
  const fisaMedicalaEntity = useAppSelector(state => state.fisaMedicala.entity);
  const loading = useAppSelector(state => state.fisaMedicala.loading);
  const updating = useAppSelector(state => state.fisaMedicala.updating);
  const updateSuccess = useAppSelector(state => state.fisaMedicala.updateSuccess);

  const handleClose = () => {
    navigate(`/fisa-medicala-med-app${location.search}`);
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
    values.dataConsultatie = convertDateTimeToServer(values.dataConsultatie);

    const entity = {
      ...fisaMedicalaEntity,
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
          dataConsultatie: displayDefaultDateTime(),
        }
      : {
          ...fisaMedicalaEntity,
          dataConsultatie: convertDateTimeFromServer(fisaMedicalaEntity.dataConsultatie),
          programare: fisaMedicalaEntity?.programare?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="medicalsystemApp.fisaMedicala.home.createOrEditLabel" data-cy="FisaMedicalaCreateUpdateHeading">
            <Translate contentKey="medicalsystemApp.fisaMedicala.home.createOrEditLabel">Create or edit a FisaMedicala</Translate>
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
                  id="fisa-medicala-med-app-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('medicalsystemApp.fisaMedicala.diagnostic')}
                id="fisa-medicala-med-app-diagnostic"
                name="diagnostic"
                data-cy="diagnostic"
                type="text"
              />
              <ValidatedField
                label={translate('medicalsystemApp.fisaMedicala.tratament')}
                id="fisa-medicala-med-app-tratament"
                name="tratament"
                data-cy="tratament"
                type="text"
              />
              <ValidatedField
                label={translate('medicalsystemApp.fisaMedicala.recomandari')}
                id="fisa-medicala-med-app-recomandari"
                name="recomandari"
                data-cy="recomandari"
                type="text"
              />
              <ValidatedField
                label={translate('medicalsystemApp.fisaMedicala.dataConsultatie')}
                id="fisa-medicala-med-app-dataConsultatie"
                name="dataConsultatie"
                data-cy="dataConsultatie"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="fisa-medicala-med-app-programare"
                name="programare"
                data-cy="programare"
                label={translate('medicalsystemApp.fisaMedicala.programare')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/fisa-medicala-med-app" replace color="info">
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

export default FisaMedicalaMedAppUpdate;
