import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './program-med-app.reducer';

export const ProgramMedAppDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const programEntity = useAppSelector(state => state.program.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="programDetailsHeading">
          <Translate contentKey="medicalsystemApp.program.detail.title">Program</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{programEntity.id}</dd>
          <dt>
            <span id="ziuaSaptamanii">
              <Translate contentKey="medicalsystemApp.program.ziuaSaptamanii">Ziua Saptamanii</Translate>
            </span>
          </dt>
          <dd>{programEntity.ziuaSaptamanii}</dd>
          <dt>
            <span id="oraStart">
              <Translate contentKey="medicalsystemApp.program.oraStart">Ora Start</Translate>
            </span>
          </dt>
          <dd>{programEntity.oraStart ? <TextFormat value={programEntity.oraStart} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="oraFinal">
              <Translate contentKey="medicalsystemApp.program.oraFinal">Ora Final</Translate>
            </span>
          </dt>
          <dd>{programEntity.oraFinal ? <TextFormat value={programEntity.oraFinal} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <Translate contentKey="medicalsystemApp.program.medic">Medic</Translate>
          </dt>
          <dd>{programEntity.medic ? programEntity.medic.id : ''}</dd>
          <dt>
            <Translate contentKey="medicalsystemApp.program.locatie">Locatie</Translate>
          </dt>
          <dd>{programEntity.locatie ? programEntity.locatie.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/program-med-app" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/program-med-app/${programEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProgramMedAppDetail;
