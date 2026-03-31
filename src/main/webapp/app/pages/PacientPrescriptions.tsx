import React, { useEffect, useState } from 'react';
import dayjs from 'dayjs';
import { getMyAppointments, getCurrentPacientByUserId, ID, Programare } from 'app/shared/api/pacient-api';
import { useAppSelector } from 'app/config/store';
import { Card, CardBody, CardHeader, Table, Badge, Button, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';

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

      <Card className="border-0 shadow-sm glass-panel overflow-hidden">
        <CardBody className="p-0">
          {appointments.length === 0 ? (
            <div className="p-5 text-center">
              <div className="mb-3 display-4 text-muted opacity-25">📋</div>
              <h5 className="text-muted">Nu aveți încă nicio rețetă sau fișă medicală înregistrată.</h5>
              <p className="text-muted small">După ce medicul va finaliza consultația, rețeta va apărea aici.</p>
            </div>
          ) : (
            <div className="table-responsive">
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
                          <div className="bg-light rounded-circle p-2 me-2 text-primary small">🩺</div>
                          <div>
                            <div className="fw-semibold">
                              {a.medic?.user ? `Dr. ${a.medic.user.lastName} ${a.medic.user.firstName}` : `Medic #${a.medicId}`}
                            </div>
                            <small className="text-muted">{a.medic?.gradProfesional || 'Medic Specialist'}</small>
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
          )}
        </CardBody>
      </Card>

      {/* Prescription View Modal */}
      <Modal isOpen={isModalOpen} toggle={toggleModal} size="lg" centered className="glass-modal">
        <ModalHeader toggle={toggleModal} className="border-0 pb-0">
          <div className="d-flex align-items-center">
            <span className="display-6 me-3">📄</span>
            <div>
              <h4 className="m-0 text-primary fw-bold">Fișă Medicală / Rețetă</h4>
              <small className="text-muted">Emisă la data de {dayjs(selectedPrescription?.dataProgramare).format('DD.MM.YYYY')}</small>
            </div>
          </div>
        </ModalHeader>
        <ModalBody className="py-4 px-4">
          <div className="row g-4">
            <div className="col-md-6">
              <div className="p-3 bg-light rounded-4 border-0">
                <label className="small text-uppercase text-muted fw-bold mb-1 d-block">Medic Curant</label>
                <div className="h6 mb-0">
                  {selectedPrescription?.medic?.user
                    ? `Dr. ${selectedPrescription.medic.user.lastName} ${selectedPrescription.medic.user.firstName}`
                    : '-'}
                </div>
                <small className="text-muted">{selectedPrescription?.medic?.gradProfesional}</small>
              </div>
            </div>
            <div className="col-md-6">
              <div className="p-3 bg-light rounded-4 border-0">
                <label className="small text-uppercase text-muted fw-bold mb-1 d-block">Locație</label>
                <div className="h6 mb-0">{selectedPrescription?.clinica?.nume}</div>
                <small className="text-muted">
                  {selectedPrescription?.clinica?.locatie?.adresa}, {selectedPrescription?.clinica?.locatie?.oras}
                </small>
              </div>
            </div>

            <div className="col-12 mt-4">
              <div className="mb-4">
                <h6 className="fw-bold text-dark d-flex align-items-center">
                  <span className="me-2 text-primary">🔍</span> Diagnostic
                </h6>
                <div className="p-3 bg-white rounded-3 border border-light-subtle shadow-xs min-h-50">
                  {selectedPrescription?.fisaMedicala?.diagnostic || 'Nespecificat'}
                </div>
              </div>

              <div className="mb-4">
                <h6 className="fw-bold text-dark d-flex align-items-center">
                  <span className="me-2 text-success">💊</span> Tratament și Rețetă
                </h6>
                <div className="p-3 bg-success bg-opacity-5 rounded-3 border border-success border-opacity-10 text-dark min-h-100 whitespace-pre-wrap">
                  {selectedPrescription?.fisaMedicala?.tratament || 'Nespecificat'}
                </div>
              </div>

              <div className="mb-0">
                <h6 className="fw-bold text-dark d-flex align-items-center">
                  <span className="me-2 text-warning">💡</span> Recomandări
                </h6>
                <div className="p-3 bg-warning bg-opacity-5 rounded-3 border border-warning border-opacity-10 text-dark min-h-50">
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
          <Button color="primary" pill onClick={() => window.print()} className="rounded-pill px-4 shadow-sm">
            🖨️ Printează Rețeta
          </Button>
        </ModalFooter>
      </Modal>
    </div>
  );
};

export default PacientPrescriptions;
