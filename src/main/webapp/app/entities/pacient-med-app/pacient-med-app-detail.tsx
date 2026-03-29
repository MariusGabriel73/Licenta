import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './pacient-med-app.reducer';

export const PacientMedAppDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const pacientEntity = useAppSelector(state => state.pacient.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="pacientDetailsHeading">
          <Translate contentKey="medicalsystemApp.pacient.detail.title">Pacient</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{pacientEntity.id}</dd>
          <dt>
            <span id="cnp">
              <Translate contentKey="medicalsystemApp.pacient.cnp">Cnp</Translate>
            </span>
          </dt>
          <dd>{pacientEntity.cnp}</dd>
          <dt>
            <span id="telefon">
              <Translate contentKey="medicalsystemApp.pacient.telefon">Telefon</Translate>
            </span>
          </dt>
          <dd>{pacientEntity.telefon}</dd>
          <dt>
            <span id="dataNasterii">
              <Translate contentKey="medicalsystemApp.pacient.dataNasterii">Data Nasterii</Translate>
            </span>
          </dt>
          <dd>
            {pacientEntity.dataNasterii ? (
              <TextFormat value={pacientEntity.dataNasterii} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="adresa">
              <Translate contentKey="medicalsystemApp.pacient.adresa">Adresa</Translate>
            </span>
          </dt>
          <dd>{pacientEntity.adresa}</dd>
          <dt>
            <Translate contentKey="medicalsystemApp.pacient.user">User</Translate>
          </dt>
          <dd>{pacientEntity.user ? pacientEntity.user.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/pacient-med-app" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/pacient-med-app/${pacientEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default PacientMedAppDetail;
