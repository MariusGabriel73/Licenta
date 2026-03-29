import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './sectie-med-app.reducer';

export const SectieMedAppDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const sectieEntity = useAppSelector(state => state.sectie.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="sectieDetailsHeading">
          <Translate contentKey="medicalsystemApp.sectie.detail.title">Sectie</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{sectieEntity.id}</dd>
          <dt>
            <span id="nume">
              <Translate contentKey="medicalsystemApp.sectie.nume">Nume</Translate>
            </span>
          </dt>
          <dd>{sectieEntity.nume}</dd>
        </dl>
        <Button tag={Link} to="/sectie-med-app" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/sectie-med-app/${sectieEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default SectieMedAppDetail;
