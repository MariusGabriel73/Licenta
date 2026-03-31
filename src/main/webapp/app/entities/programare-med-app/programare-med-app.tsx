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

import { getEntities } from './programare-med-app.reducer';

export const ProgramareMedApp = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const programareList = useAppSelector(state => state.programare.entities);
  const loading = useAppSelector(state => state.programare.loading);
  const totalItems = useAppSelector(state => state.programare.totalItems);

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
      <h2 id="programare-med-app-heading" data-cy="ProgramareHeading">
        <Translate contentKey="medicalsystemApp.programare.home.title">Programares</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="medicalsystemApp.programare.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/programare-med-app/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="medicalsystemApp.programare.home.createLabel">Create new Programare</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {programareList && programareList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="medicalsystemApp.programare.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('dataProgramare')}>
                  <Translate contentKey="medicalsystemApp.programare.dataProgramare">Data Programare</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('dataProgramare')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="medicalsystemApp.programare.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('observatii')}>
                  <Translate contentKey="medicalsystemApp.programare.observatii">Observatii</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('observatii')} />
                </th>
                <th>
                  <Translate contentKey="medicalsystemApp.programare.pacient">Pacient</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="medicalsystemApp.programare.medic">Medic</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="medicalsystemApp.programare.clinica">Clinica</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {programareList.map((programare, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/programare-med-app/${programare.id}`} color="link" size="sm">
                      {programare.id}
                    </Button>
                  </td>
                  <td>
                    {programare.dataProgramare ? (
                      <TextFormat type="date" value={programare.dataProgramare} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    <Translate contentKey={`medicalsystemApp.ProgramareStatus.${programare.status}`} />
                  </td>
                  <td>{programare.observatii}</td>
                  <td>
                    {programare.pacient ? (
                      <Link to={`/pacient-med-app/${programare.pacient.id}`}>
                        {programare.pacient.user?.lastName || programare.pacient.user?.firstName
                          ? `${programare.pacient.user.lastName || ''} ${programare.pacient.user.firstName || ''}`.trim()
                          : programare.pacient.user?.login
                            ? `Pacient: ${programare.pacient.user.login}`
                            : programare.pacient.cnp
                              ? `Pacient (CNP: ${programare.pacient.cnp})`
                              : `Pacient #${programare.pacient.id}`}
                      </Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {programare.medic ? (
                      <Link to={`/medic-med-app/${programare.medic.id}`}>
                        {programare.medic.user
                          ? `Dr. ${programare.medic.user.lastName} ${programare.medic.user.firstName}`
                          : `Medic (${programare.medic.gradProfesional})`}
                      </Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {programare.clinica ? <Link to={`/clinica-med-app/${programare.clinica.id}`}>{programare.clinica.nume}</Link> : ''}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/programare-med-app/${programare.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/programare-med-app/${programare.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/programare-med-app/${programare.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="medicalsystemApp.programare.home.notFound">No Programares found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={programareList && programareList.length > 0 ? '' : 'd-none'}>
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

export default ProgramareMedApp;
