import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './fisa-medicala-med-app.reducer';

export const FisaMedicalaMedAppDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const fisaMedicalaEntity = useAppSelector(state => state.fisaMedicala.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="fisaMedicalaDetailsHeading">
          <Translate contentKey="medicalsystemApp.fisaMedicala.detail.title">FisaMedicala</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{fisaMedicalaEntity.id}</dd>
          <dt>
            <span id="diagnostic">
              <Translate contentKey="medicalsystemApp.fisaMedicala.diagnostic">Diagnostic</Translate>
            </span>
          </dt>
          <dd>{fisaMedicalaEntity.diagnostic}</dd>
          <dt>
            <span id="tratament">
              <Translate contentKey="medicalsystemApp.fisaMedicala.tratament">Tratament</Translate>
            </span>
          </dt>
          <dd>{fisaMedicalaEntity.tratament}</dd>
          <dt>
            <span id="recomandari">
              <Translate contentKey="medicalsystemApp.fisaMedicala.recomandari">Recomandari</Translate>
            </span>
          </dt>
          <dd>{fisaMedicalaEntity.recomandari}</dd>
          <dt>
            <span id="dataConsultatie">
              <Translate contentKey="medicalsystemApp.fisaMedicala.dataConsultatie">Data Consultatie</Translate>
            </span>
          </dt>
          <dd>
            {fisaMedicalaEntity.dataConsultatie ? (
              <TextFormat value={fisaMedicalaEntity.dataConsultatie} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="medicalsystemApp.fisaMedicala.programare">Programare</Translate>
          </dt>
          <dd>{fisaMedicalaEntity.programare ? fisaMedicalaEntity.programare.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/fisa-medicala-med-app" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/fisa-medicala-med-app/${fisaMedicalaEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default FisaMedicalaMedAppDetail;
