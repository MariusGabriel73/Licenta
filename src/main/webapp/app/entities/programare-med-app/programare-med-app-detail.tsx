import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './programare-med-app.reducer';

export const ProgramareMedAppDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const programareEntity = useAppSelector(state => state.programare.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="programareDetailsHeading">
          <Translate contentKey="medicalsystemApp.programare.detail.title">Programare</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{programareEntity.id}</dd>
          <dt>
            <span id="dataProgramare">
              <Translate contentKey="medicalsystemApp.programare.dataProgramare">Data Programare</Translate>
            </span>
          </dt>
          <dd>
            {programareEntity.dataProgramare ? (
              <TextFormat value={programareEntity.dataProgramare} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="status">
              <Translate contentKey="medicalsystemApp.programare.status">Status</Translate>
            </span>
          </dt>
          <dd>{programareEntity.status}</dd>
          <dt>
            <span id="observatii">
              <Translate contentKey="medicalsystemApp.programare.observatii">Observatii</Translate>
            </span>
          </dt>
          <dd>{programareEntity.observatii}</dd>
          <dt>
            <Translate contentKey="medicalsystemApp.programare.pacient">Pacient</Translate>
          </dt>
          <dd>{programareEntity.pacient ? programareEntity.pacient.id : ''}</dd>
          <dt>
            <Translate contentKey="medicalsystemApp.programare.medic">Medic</Translate>
          </dt>
          <dd>{programareEntity.medic ? programareEntity.medic.id : ''}</dd>
          <dt>
            <Translate contentKey="medicalsystemApp.programare.clinica">Clinica</Translate>
          </dt>
          <dd>{programareEntity.clinica ? programareEntity.clinica.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/programare-med-app" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/programare-med-app/${programareEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProgramareMedAppDetail;
