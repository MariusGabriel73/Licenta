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

import { getEntities } from './fisa-medicala-med-app.reducer';

export const FisaMedicalaMedApp = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const fisaMedicalaList = useAppSelector(state => state.fisaMedicala.entities);
  const loading = useAppSelector(state => state.fisaMedicala.loading);
  const totalItems = useAppSelector(state => state.fisaMedicala.totalItems);

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
      <h2 id="fisa-medicala-med-app-heading" data-cy="FisaMedicalaHeading">
        <Translate contentKey="medicalsystemApp.fisaMedicala.home.title">Fisa Medicalas</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="medicalsystemApp.fisaMedicala.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/fisa-medicala-med-app/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="medicalsystemApp.fisaMedicala.home.createLabel">Create new Fisa Medicala</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {fisaMedicalaList && fisaMedicalaList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="medicalsystemApp.fisaMedicala.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('diagnostic')}>
                  <Translate contentKey="medicalsystemApp.fisaMedicala.diagnostic">Diagnostic</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('diagnostic')} />
                </th>
                <th className="hand" onClick={sort('tratament')}>
                  <Translate contentKey="medicalsystemApp.fisaMedicala.tratament">Tratament</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('tratament')} />
                </th>
                <th className="hand" onClick={sort('recomandari')}>
                  <Translate contentKey="medicalsystemApp.fisaMedicala.recomandari">Recomandari</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('recomandari')} />
                </th>
                <th className="hand" onClick={sort('dataConsultatie')}>
                  <Translate contentKey="medicalsystemApp.fisaMedicala.dataConsultatie">Data Consultatie</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('dataConsultatie')} />
                </th>
                <th>
                  <Translate contentKey="medicalsystemApp.fisaMedicala.programare">Programare</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {fisaMedicalaList.map((fisaMedicala, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/fisa-medicala-med-app/${fisaMedicala.id}`} color="link" size="sm">
                      {fisaMedicala.id}
                    </Button>
                  </td>
                  <td>{fisaMedicala.diagnostic}</td>
                  <td>{fisaMedicala.tratament}</td>
                  <td>{fisaMedicala.recomandari}</td>
                  <td>
                    {fisaMedicala.dataConsultatie ? (
                      <TextFormat type="date" value={fisaMedicala.dataConsultatie} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {fisaMedicala.programare ? (
                      <Link to={`/programare-med-app/${fisaMedicala.programare.id}`}>{fisaMedicala.programare.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/fisa-medicala-med-app/${fisaMedicala.id}`}
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
                        to={`/fisa-medicala-med-app/${fisaMedicala.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/fisa-medicala-med-app/${fisaMedicala.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="medicalsystemApp.fisaMedicala.home.notFound">No Fisa Medicalas found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={fisaMedicalaList && fisaMedicalaList.length > 0 ? '' : 'd-none'}>
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

export default FisaMedicalaMedApp;
