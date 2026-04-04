import React, { useEffect, useState } from 'react';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button, Form, FormGroup, Label, Input, Alert, FormText } from 'reactstrap';
import { faInfoCircle } from '@fortawesome/free-solid-svg-icons';
import axios from 'axios';
import { toast } from 'react-toastify';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faNotesMedical, faStethoscope, faPills, faLightbulb, faSave, faTimes } from '@fortawesome/free-solid-svg-icons';

export interface IReportFormModalProps {
  isOpen: boolean;
  toggle: () => void;
  programare: any;
  onSuccess: () => void;
}

const ReportFormModal = ({ isOpen, toggle, programare, onSuccess }: IReportFormModalProps) => {
  const [loading, setLoading] = useState(false);
  const [diagnostic, setDiagnostic] = useState('');
  const [tratament, setTratament] = useState('');
  const [recomandari, setRecomandari] = useState('');
  const [error, setError] = useState(null);

  useEffect(() => {
    if (isOpen && programare?.fisaMedicala) {
      setDiagnostic(programare.fisaMedicala.diagnostic || '');
      setTratament(programare.fisaMedicala.tratament || '');
      setRecomandari(programare.fisaMedicala.recomandari || '');
    } else if (isOpen) {
      setDiagnostic('');
      setTratament('');
      setRecomandari('');
    }
  }, [isOpen, programare]);

  const handleSave = async () => {
    if (!diagnostic || !tratament) {
      setError('Diagnosticul și tratamentul sunt obligatorii.');
      return;
    }

    setLoading(true);
    setError(null);

    const fisa = {
      id: programare?.fisaMedicala?.id || null,
      diagnostic,
      tratament,
      recomandari,
      dataConsultatie: new Date().toISOString(),
      programare: { id: programare.id },
    };

    try {
      if (fisa.id) {
        await axios.put(`/api/fisa-medicalas/${fisa.id}`, fisa);
        toast.success('Fișa medicală a fost actualizată cu succes!');
      } else {
        await axios.post('/api/fisa-medicalas', fisa);
        toast.success('Fișa medicală a fost creată cu succes!');
      }
      onSuccess();
      toggle();
    } catch (err) {
      console.error(err);
      setError('A apărut o eroare la salvarea fișei medicale.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal isOpen={isOpen} toggle={toggle} size="lg" centered className="glass-modal custom-report-modal">
      <ModalHeader toggle={toggle} className="border-0 pb-0">
        <div className="d-flex align-items-center">
          <div className="bg-soft-primary p-3 rounded-4 me-3 text-primary shadow-sm">
            <FontAwesomeIcon icon={faNotesMedical} size="lg" />
          </div>
          <div>
            <h4 className="m-0 text-dark fw-bold">{programare?.fisaMedicala ? 'Editare Fișă Medicală' : 'Creare Fișă Medicală'}</h4>
            <div className="d-flex align-items-center mt-1">
              <span className="badge bg-light text-secondary border rounded-pill px-2">
                Pacient: {programare?.pacient?.user?.lastName} {programare?.pacient?.user?.firstName}
              </span>
            </div>
          </div>
        </div>
      </ModalHeader>
      <ModalBody className="py-4">
        {error && (
          <Alert color="danger" className="rounded-pill px-4 shadow-sm mb-4 border-0">
            {error}
          </Alert>
        )}
        <Form className="px-1">
          <FormGroup className="mb-4">
            <Label for="diagnostic" className="fw-bold text-dark mb-2 d-flex align-items-center">
              <FontAwesomeIcon icon={faStethoscope} className="text-primary me-2 opacity-75" />
              Diagnostic
            </Label>
            <Input
              type="textarea"
              id="diagnostic"
              rows={3}
              placeholder="Introduceți diagnosticul determinat în urma consultației..."
              value={diagnostic}
              onChange={e => setDiagnostic(e.target.value)}
              className="rounded-4 border-light-subtle shadow-xs focus-ring-primary p-3"
            />
          </FormGroup>
          <FormGroup className="mb-4">
            <Label for="tratament" className="fw-bold text-dark mb-2 d-flex align-items-center">
              <FontAwesomeIcon icon={faPills} className="text-success me-2 opacity-75" />
              Tratament (Rețetă)
            </Label>
            <Input
              type="textarea"
              id="tratament"
              rows={5}
              placeholder="Specificați medicamentele, dozajul și durata tratamentului..."
              value={tratament}
              onChange={e => setTratament(e.target.value)}
              className="rounded-4 border-light-subtle shadow-xs focus-ring-success p-3"
            />
            <FormText className="mt-2 d-flex align-items-center text-primary-emphasis bg-soft-primary p-2 rounded-3 small">
              <FontAwesomeIcon icon={faInfoCircle} className="me-2" />
              <span>
                <strong>Sfat:</strong> Pentru a activa dozele în calendarul pacientului, scrieți pe linii separate, de ex:{' '}
                <em>&quot;Nurofen 1 pe zi 5 zile&quot;</em> sau <em>&quot;Nurofen la 8 ore 3 zile&quot;</em>.
              </span>
            </FormText>
          </FormGroup>
          <FormGroup className="mb-0">
            <Label for="recomandari" className="fw-bold text-dark mb-2 d-flex align-items-center">
              <FontAwesomeIcon icon={faLightbulb} className="text-warning me-2 opacity-75" />
              Recomandări suplimentare
            </Label>
            <Input
              type="textarea"
              id="recomandari"
              rows={3}
              placeholder="Alte indicații, regim alimentar sau analize ulterioare..."
              value={recomandari}
              onChange={e => setRecomandari(e.target.value)}
              className="rounded-4 border-light-subtle shadow-xs focus-ring-warning p-3"
            />
          </FormGroup>
        </Form>
      </ModalBody>
      <ModalFooter className="border-0 bg-light bg-opacity-50 py-3">
        <Button color="secondary" outline pill onClick={toggle} className="rounded-pill px-4 border-0">
          <FontAwesomeIcon icon={faTimes} className="me-2" />
          Anulează
        </Button>
        <Button color="primary" pill onClick={handleSave} disabled={loading} className="rounded-pill px-4 shadow-sm ms-2 fw-bold">
          <FontAwesomeIcon icon={faSave} className="me-2" />
          {loading ? 'Se salvează...' : 'Salvează Fișa'}
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default ReportFormModal;
