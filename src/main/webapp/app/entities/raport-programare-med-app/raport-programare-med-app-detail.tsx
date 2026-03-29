import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './raport-programare-med-app.reducer';

export const RaportProgramareMedAppDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const raportProgramareEntity = useAppSelector(state => state.raportProgramare.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="raportProgramareDetailsHeading">
          <Translate contentKey="medicalsystemApp.raportProgramare.detail.title">RaportProgramare</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{raportProgramareEntity.id}</dd>
          <dt>
            <span id="oraProgramata">
              <Translate contentKey="medicalsystemApp.raportProgramare.oraProgramata">Ora Programata</Translate>
            </span>
          </dt>
          <dd>
            {raportProgramareEntity.oraProgramata ? (
              <TextFormat value={raportProgramareEntity.oraProgramata} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="oraInceputConsultatie">
              <Translate contentKey="medicalsystemApp.raportProgramare.oraInceputConsultatie">Ora Inceput Consultatie</Translate>
            </span>
          </dt>
          <dd>
            {raportProgramareEntity.oraInceputConsultatie ? (
              <TextFormat value={raportProgramareEntity.oraInceputConsultatie} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="durataConsultatie">
              <Translate contentKey="medicalsystemApp.raportProgramare.durataConsultatie">Durata Consultatie</Translate>
            </span>
          </dt>
          <dd>{raportProgramareEntity.durataConsultatie}</dd>
          <dt>
            <Translate contentKey="medicalsystemApp.raportProgramare.programare">Programare</Translate>
          </dt>
          <dd>{raportProgramareEntity.programare ? raportProgramareEntity.programare.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/raport-programare-med-app" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/raport-programare-med-app/${raportProgramareEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default RaportProgramareMedAppDetail;
