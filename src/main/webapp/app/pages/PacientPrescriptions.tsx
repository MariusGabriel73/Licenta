import React, { useEffect, useState } from 'react';
import dayjs from 'dayjs';
import { getMyAppointments, getCurrentPacientByUserId, ID, Programare } from 'app/shared/api/pacient-api';
import { useAppSelector } from 'app/config/store';
import { Card, CardBody, CardHeader, Table, Badge, Button, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faFileMedical,
  faStethoscope,
  faPills,
  faLightbulb,
  faPrint,
  faUserMd,
  faHospital,
  faNotesMedical,
} from '@fortawesome/free-solid-svg-icons';

const PacientPrescriptions = () => {
  const account = useAppSelector(state => state.authentication.account);
  const [appointments, setAppointments] = useState<Programare[]>([]);
  const [loading, setLoading] = useState(false);
  const [selectedPrescription, setSelectedPrescription] = useState<Programare | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  useEffect(() => {
    if (account?.id) {
      (async () => {
        setLoading(true);
        try {
          const pacient = await getCurrentPacientByUserId(account.id);
          if (pacient?.id) {
            const my = await getMyAppointments(pacient.id);
            // Filter only those with medical records
            setAppointments(my.filter(a => a.fisaMedicala));
          }
        } finally {
          setLoading(false);
        }
      })();
    }
  }, [account]);

  const toggleModal = () => setIsModalOpen(!isModalOpen);

  const viewPrescription = (a: Programare) => {
    setSelectedPrescription(a);
    toggleModal();
  };

  return (
    <div className="container py-5 fade-in-up">
      <div className="d-flex align-items-center mb-4">
        <div className="bg-primary bg-opacity-10 p-3 rounded-circle me-3">
          <h2 className="m-0 text-primary">📑</h2>
        </div>
        <div>
          <h2 className="m-0 fw-bold">Istoric Rețete și Consultări</h2>
          <p className="text-muted mb-0">Toate prescripțiile tale medicale într-un singur loc</p>
        </div>
      </div>

      <Card className="border-0 shadow-sm glass-panel overflow-hidden rounded-4">
        <CardBody className="p-0">
          {appointments.length === 0 ? (
            <div className="p-5 text-center">
              <div className="mb-3 display-4 text-muted opacity-25">
                <FontAwesomeIcon icon={faNotesMedical} />
              </div>
              <h5 className="text-muted">Nu aveți încă nicio rețetă sau fișă medicală înregistrată.</h5>
              <p className="text-muted small">După ce medicul va finaliza consultația, rețeta va apărea aici.</p>
            </div>
          ) : (
            <>
              {/* DESKTOP VIEW */}
              <div className="table-responsive d-none d-md-block">
                <Table hover borderless className="align-middle mb-0">
                  <thead className="bg-light bg-opacity-50">
                    <tr>
                      <th className="px-4 py-3 text-secondary small text-uppercase fw-bold">Data</th>
                      <th className="py-3 text-secondary small text-uppercase fw-bold">Medic</th>
                      <th className="py-3 text-secondary small text-uppercase fw-bold">Clinică</th>
                      <th className="py-3 text-secondary small text-uppercase fw-bold">Diagnostic</th>
                      <th className="py-3 text-end px-4 text-secondary small text-uppercase fw-bold">Acțiuni</th>
                    </tr>
                  </thead>
                  <tbody>
                    {appointments.map(a => (
                      <tr key={a.id} className="border-top border-light cursor-pointer" onClick={() => viewPrescription(a)}>
                        <td className="px-4 py-3">
                          <div className="fw-bold">{dayjs(a.dataProgramare).format('DD MMM YYYY')}</div>
                          <small className="text-muted">{dayjs(a.dataProgramare).format('HH:mm')}</small>
                        </td>
                        <td className="py-3">
                          <div className="d-flex align-items-center">
                            <div
                              className="bg-soft-primary rounded-circle p-2 me-2 text-primary small d-flex align-items-center justify-content-center"
                              style={{ width: '32px', height: '32px' }}
                            >
                              <FontAwesomeIcon icon={faUserMd} />
                            </div>
                            <div>
                              <div className="fw-semibold">
                                {a.medic?.user ? `Dr. ${a.medic.user.lastName} ${a.medic.user.firstName}` : `Medic #${a.medicId}`}
                              </div>
                              <small className="text-muted small-text">{a.medic?.gradProfesional || 'Medic Specialist'}</small>
                            </div>
                          </div>
                        </td>
                        <td className="py-3">
                          <div className="fw-semibold">{a.clinica?.nume || 'Clinică Medicală'}</div>
                          <small className="text-muted">{a.clinica?.locatie?.oras || '-'}</small>
                        </td>
                        <td className="py-3">
                          <Badge color="info" pill className="fw-normal bg-opacity-10 text-info border-0 px-3">
                            {a.fisaMedicala?.diagnostic?.substring(0, 30) || 'Diagnostic...'}
                            {(a.fisaMedicala?.diagnostic?.length || 0) > 30 ? '...' : ''}
                          </Badge>
                        </td>
                        <td className="text-end px-4 py-3">
                          <Button
                            color="primary"
                            size="sm"
                            outline
                            pill
                            className="rounded-pill px-4 border-0 shadow-sm bg-primary bg-opacity-10 text-primary"
                          >
                            Vezi Detalii
                          </Button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </Table>
              </div>

              {/* MOBILE VIEW */}
              <div className="d-block d-md-none p-3">
                <div className="d-flex flex-column gap-3">
                  {appointments.map(a => (
                    <div
                      key={a.id}
                      className="bg-light bg-opacity-50 rounded-4 p-3 border border-light-subtle shadow-sm cursor-pointer"
                      onClick={() => viewPrescription(a)}
                    >
                      <div className="d-flex justify-content-between align-items-center mb-3">
                        <div className="d-flex align-items-center">
                          <h3 className="m-0 text-primary me-3 fw-bold">{dayjs(a.dataProgramare).format('DD')}</h3>
                          <div>
                            <div className="fw-bold text-dark">{dayjs(a.dataProgramare).format('MMM YYYY')}</div>
                            <small className="text-muted">{dayjs(a.dataProgramare).format('HH:mm')}</small>
                          </div>
                        </div>
                        <Badge color="info" pill className="fw-normal bg-opacity-10 text-info border-0 px-3 py-2">
                          {a.fisaMedicala?.diagnostic?.substring(0, 20) || 'Diagnostic...'}
                          {(a.fisaMedicala?.diagnostic?.length || 0) > 20 ? '...' : ''}
                        </Badge>
                      </div>

                      <div className="d-flex flex-column gap-2 mb-3 bg-white p-2 rounded-3 border border-light">
                        <div className="d-flex align-items-center">
                          <div
                            className="bg-soft-primary rounded-circle p-2 me-2 text-primary small d-flex align-items-center justify-content-center"
                            style={{ width: '28px', height: '28px' }}
                          >
                            <FontAwesomeIcon icon={faUserMd} />
                          </div>
                          <div>
                            <div className="fw-semibold small text-dark">
                              {a.medic?.user ? `Dr. ${a.medic.user.lastName} ${a.medic.user.firstName}` : `Medic`}
                            </div>
                          </div>
                        </div>

                        <div className="d-flex align-items-center">
                          <div
                            className="bg-soft-secondary rounded-circle p-2 me-2 text-secondary small d-flex align-items-center justify-content-center"
                            style={{ width: '28px', height: '28px' }}
                          >
                            <FontAwesomeIcon icon={faHospital} />
                          </div>
                          <div className="small text-muted">{a.clinica?.nume || 'Clinică Medicală'}</div>
                        </div>
                      </div>

                      <Button
                        color="primary"
                        size="sm"
                        outline
                        pill
                        className="w-100 rounded-pill border-0 shadow-sm bg-white text-primary"
                      >
                        Vezi Detalii Rețetă
                      </Button>
                    </div>
                  ))}
                </div>
              </div>
            </>
          )}
        </CardBody>
      </Card>

      {/* Prescription View Modal */}
      <Modal isOpen={isModalOpen} toggle={toggleModal} size="lg" centered className="glass-modal custom-prescription-modal">
        <ModalHeader toggle={toggleModal} className="border-0 pb-0">
          <div className="d-flex align-items-center">
            <div className="bg-soft-primary p-3 rounded-4 me-3 text-primary shadow-sm">
              <FontAwesomeIcon icon={faFileMedical} size="lg" />
            </div>
            <div>
              <h4 className="m-0 text-dark fw-bold">Fișă Medicală / Rețetă</h4>
              <div className="d-flex align-items-center mt-1">
                <span className="badge bg-light text-secondary border rounded-pill small-text px-2">
                  Emisă la data de {dayjs(selectedPrescription?.dataProgramare).format('DD.MM.YYYY')}
                </span>
              </div>
            </div>
          </div>
        </ModalHeader>
        <ModalBody className="py-4 px-4">
          <div className="row g-4">
            <div className="col-md-6">
              <div className="p-4 bg-white rounded-4 border border-light-subtle shadow-xs h-100 transition-hover">
                <div className="d-flex align-items-center mb-2">
                  <FontAwesomeIcon icon={faUserMd} className="text-primary opacity-50 me-2" />
                  <label className="small text-uppercase text-muted fw-bold mb-0">Medic Curant</label>
                </div>
                <div className="h5 mb-0 fw-bold text-dark">
                  {selectedPrescription?.medic?.user
                    ? `Dr. ${selectedPrescription.medic.user.lastName} ${selectedPrescription.medic.user.firstName}`
                    : '-'}
                </div>
                <div className="text-primary small fw-semibold mt-1">{selectedPrescription?.medic?.gradProfesional}</div>
              </div>
            </div>
            <div className="col-md-6">
              <div className="p-4 bg-white rounded-4 border border-light-subtle shadow-xs h-100 transition-hover">
                <div className="d-flex align-items-center mb-2">
                  <FontAwesomeIcon icon={faHospital} className="text-primary opacity-50 me-2" />
                  <label className="small text-uppercase text-muted fw-bold mb-0">Locație</label>
                </div>
                <div className="h5 mb-0 fw-bold text-dark">{selectedPrescription?.clinica?.nume}</div>
                <div className="text-muted small mt-1">
                  {selectedPrescription?.clinica?.locatie?.adresa}, {selectedPrescription?.clinica?.locatie?.oras}
                </div>
              </div>
            </div>

            <div className="col-12 mt-4">
              <div className="mb-4">
                <h6 className="fw-bold text-dark d-flex align-items-center mb-3">
                  <div
                    className="bg-soft-primary p-2 rounded-3 me-2 text-primary d-flex align-items-center justify-content-center"
                    style={{ width: '32px', height: '32px' }}
                  >
                    <FontAwesomeIcon icon={faStethoscope} />
                  </div>
                  Diagnostic
                </h6>
                <div className="p-4 bg-white rounded-4 border border-light-subtle shadow-xs min-h-50 border-start border-4 border-start-primary">
                  {selectedPrescription?.fisaMedicala?.diagnostic || 'Nespecificat'}
                </div>
              </div>

              <div className="mb-4">
                <h6 className="fw-bold text-dark d-flex align-items-center mb-3">
                  <div
                    className="bg-soft-success p-2 rounded-3 me-2 text-success d-flex align-items-center justify-content-center"
                    style={{ width: '32px', height: '32px' }}
                  >
                    <FontAwesomeIcon icon={faPills} />
                  </div>
                  Tratament și Rețetă
                </h6>
                <div className="p-4 bg-light bg-opacity-50 rounded-4 border border-light-subtle text-dark min-h-100 whitespace-pre-wrap border-start border-4 border-start-success shadow-xs">
                  {selectedPrescription?.fisaMedicala?.tratament || 'Nespecificat'}
                </div>
              </div>

              <div className="mb-0">
                <h6 className="fw-bold text-dark d-flex align-items-center mb-3">
                  <div
                    className="bg-soft-warning p-2 rounded-3 me-2 text-warning d-flex align-items-center justify-content-center"
                    style={{ width: '32px', height: '32px' }}
                  >
                    <FontAwesomeIcon icon={faLightbulb} />
                  </div>
                  Recomandări
                </h6>
                <div className="p-4 bg-light bg-opacity-50 rounded-4 border border-light-subtle text-dark min-h-50 border-start border-4 border-start-warning shadow-xs">
                  {selectedPrescription?.fisaMedicala?.recomandari || 'Nicio recomandare suplimentară.'}
                </div>
              </div>
            </div>
          </div>
        </ModalBody>
        <ModalFooter className="border-0 bg-light bg-opacity-50 py-3">
          <Button color="secondary" outline pill onClick={toggleModal} className="rounded-pill px-4 border-0">
            Închide
          </Button>
          <Button color="primary" pill onClick={() => window.print()} className="rounded-pill px-4 shadow-sm fw-bold">
            <FontAwesomeIcon icon={faPrint} className="me-2" />
            Printează Rețeta
          </Button>
        </ModalFooter>
      </Modal>
    </div>
  );
};

export default PacientPrescriptions;
