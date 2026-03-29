import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './clinica-med-app.reducer';

export const ClinicaMedAppDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const clinicaEntity = useAppSelector(state => state.clinica.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="clinicaDetailsHeading">
          <Translate contentKey="medicalsystemApp.clinica.detail.title">Clinica</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{clinicaEntity.id}</dd>
          <dt>
            <span id="nume">
              <Translate contentKey="medicalsystemApp.clinica.nume">Nume</Translate>
            </span>
          </dt>
          <dd>{clinicaEntity.nume}</dd>
          <dt>
            <span id="telefon">
              <Translate contentKey="medicalsystemApp.clinica.telefon">Telefon</Translate>
            </span>
          </dt>
          <dd>{clinicaEntity.telefon}</dd>
          <dt>
            <span id="email">
              <Translate contentKey="medicalsystemApp.clinica.email">Email</Translate>
            </span>
          </dt>
          <dd>{clinicaEntity.email}</dd>
          <dt>
            <Translate contentKey="medicalsystemApp.clinica.locatie">Locatie</Translate>
          </dt>
          <dd>{clinicaEntity.locatie ? clinicaEntity.locatie.id : ''}</dd>
          <dt>
            <Translate contentKey="medicalsystemApp.clinica.medici">Medici</Translate>
          </dt>
          <dd>
            {clinicaEntity.medicis
              ? clinicaEntity.medicis.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {clinicaEntity.medicis && i === clinicaEntity.medicis.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/clinica-med-app" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/clinica-med-app/${clinicaEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ClinicaMedAppDetail;
