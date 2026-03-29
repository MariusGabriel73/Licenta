import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './locatie-med-app.reducer';

export const LocatieMedAppDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const locatieEntity = useAppSelector(state => state.locatie.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="locatieDetailsHeading">
          <Translate contentKey="medicalsystemApp.locatie.detail.title">Locatie</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{locatieEntity.id}</dd>
          <dt>
            <span id="oras">
              <Translate contentKey="medicalsystemApp.locatie.oras">Oras</Translate>
            </span>
          </dt>
          <dd>{locatieEntity.oras}</dd>
          <dt>
            <span id="adresa">
              <Translate contentKey="medicalsystemApp.locatie.adresa">Adresa</Translate>
            </span>
          </dt>
          <dd>{locatieEntity.adresa}</dd>
          <dt>
            <span id="codPostal">
              <Translate contentKey="medicalsystemApp.locatie.codPostal">Cod Postal</Translate>
            </span>
          </dt>
          <dd>{locatieEntity.codPostal}</dd>
        </dl>
        <Button tag={Link} to="/locatie-med-app" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/locatie-med-app/${locatieEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default LocatieMedAppDetail;
