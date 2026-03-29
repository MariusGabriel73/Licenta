import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './raport-programare-med-app.reducer';

export const RaportProgramareMedApp = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const raportProgramareList = useAppSelector(state => state.raportProgramare.entities);
  const loading = useAppSelector(state => state.raportProgramare.loading);
  const totalItems = useAppSelector(state => state.raportProgramare.totalItems);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="raport-programare-med-app-heading" data-cy="RaportProgramareHeading">
        <Translate contentKey="medicalsystemApp.raportProgramare.home.title">Raport Programares</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="medicalsystemApp.raportProgramare.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/raport-programare-med-app/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="medicalsystemApp.raportProgramare.home.createLabel">Create new Raport Programare</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {raportProgramareList && raportProgramareList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="medicalsystemApp.raportProgramare.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('oraProgramata')}>
                  <Translate contentKey="medicalsystemApp.raportProgramare.oraProgramata">Ora Programata</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('oraProgramata')} />
                </th>
                <th className="hand" onClick={sort('oraInceputConsultatie')}>
                  <Translate contentKey="medicalsystemApp.raportProgramare.oraInceputConsultatie">Ora Inceput Consultatie</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('oraInceputConsultatie')} />
                </th>
                <th className="hand" onClick={sort('durataConsultatie')}>
                  <Translate contentKey="medicalsystemApp.raportProgramare.durataConsultatie">Durata Consultatie</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('durataConsultatie')} />
                </th>
                <th>
                  <Translate contentKey="medicalsystemApp.raportProgramare.programare">Programare</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {raportProgramareList.map((raportProgramare, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/raport-programare-med-app/${raportProgramare.id}`} color="link" size="sm">
                      {raportProgramare.id}
                    </Button>
                  </td>
                  <td>
                    {raportProgramare.oraProgramata ? (
                      <TextFormat type="date" value={raportProgramare.oraProgramata} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {raportProgramare.oraInceputConsultatie ? (
                      <TextFormat type="date" value={raportProgramare.oraInceputConsultatie} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{raportProgramare.durataConsultatie}</td>
                  <td>
                    {raportProgramare.programare ? (
                      <Link to={`/programare-med-app/${raportProgramare.programare.id}`}>{raportProgramare.programare.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/raport-programare-med-app/${raportProgramare.id}`}
                        color="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                      >
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/raport-programare-med-app/${raportProgramare.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() =>
                          (window.location.href = `/raport-programare-med-app/${raportProgramare.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                        }
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="medicalsystemApp.raportProgramare.home.notFound">No Raport Programares found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={raportProgramareList && raportProgramareList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default RaportProgramareMedApp;
