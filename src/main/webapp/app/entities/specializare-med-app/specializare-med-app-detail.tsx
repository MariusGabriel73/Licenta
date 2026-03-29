import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './specializare-med-app.reducer';

export const SpecializareMedAppDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const specializareEntity = useAppSelector(state => state.specializare.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="specializareDetailsHeading">
          <Translate contentKey="medicalsystemApp.specializare.detail.title">Specializare</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{specializareEntity.id}</dd>
          <dt>
            <span id="nume">
              <Translate contentKey="medicalsystemApp.specializare.nume">Nume</Translate>
            </span>
          </dt>
          <dd>{specializareEntity.nume}</dd>
          <dt>
            <Translate contentKey="medicalsystemApp.specializare.medici">Medici</Translate>
          </dt>
          <dd>
            {specializareEntity.medicis
              ? specializareEntity.medicis.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {specializareEntity.medicis && i === specializareEntity.medicis.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/specializare-med-app" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/specializare-med-app/${specializareEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default SpecializareMedAppDetail;
