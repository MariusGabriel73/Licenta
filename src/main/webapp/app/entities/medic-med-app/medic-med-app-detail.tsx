import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './medic-med-app.reducer';

export const MedicMedAppDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const medicEntity = useAppSelector(state => state.medic.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="medicDetailsHeading">
          <Translate contentKey="medicalsystemApp.medic.detail.title">Medic</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{medicEntity.id}</dd>
          <dt>
            <span id="gradProfesional">
              <Translate contentKey="medicalsystemApp.medic.gradProfesional">Grad Profesional</Translate>
            </span>
          </dt>
          <dd>{medicEntity.gradProfesional}</dd>
          <dt>
            <span id="telefon">
              <Translate contentKey="medicalsystemApp.medic.telefon">Telefon</Translate>
            </span>
          </dt>
          <dd>{medicEntity.telefon}</dd>
          <dt>
            <span id="disponibil">
              <Translate contentKey="medicalsystemApp.medic.disponibil">Disponibil</Translate>
            </span>
          </dt>
          <dd>{medicEntity.disponibil ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="medicalsystemApp.medic.user">User</Translate>
          </dt>
          <dd>{medicEntity.user ? medicEntity.user.id : ''}</dd>
          <dt>
            <Translate contentKey="medicalsystemApp.medic.sectie">Sectie</Translate>
          </dt>
          <dd>{medicEntity.sectie ? medicEntity.sectie.id : ''}</dd>
          <dt>
            <Translate contentKey="medicalsystemApp.medic.clinici">Clinici</Translate>
          </dt>
          <dd>
            {medicEntity.clinicis
              ? medicEntity.clinicis.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {medicEntity.clinicis && i === medicEntity.clinicis.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
          <dt>
            <Translate contentKey="medicalsystemApp.medic.specializari">Specializari</Translate>
          </dt>
          <dd>
            {medicEntity.specializaris
              ? medicEntity.specializaris.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {medicEntity.specializaris && i === medicEntity.specializaris.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/medic-med-app" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/medic-med-app/${medicEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default MedicMedAppDetail;
