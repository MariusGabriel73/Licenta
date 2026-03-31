import React, { useEffect, useState } from 'react';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button, Form, FormGroup, Label, Input, Alert } from 'reactstrap';
import axios from 'axios';
import { toast } from 'react-toastify';

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
    <Modal isOpen={isOpen} toggle={toggle} size="lg" className="glass-modal">
      <ModalHeader toggle={toggle} className="border-0">
        <h4 className="m-0 text-primary">{programare?.fisaMedicala ? '📝 Editare Fișă Medicală' : '🆕 Creare Fișă Medicală'}</h4>
        <small className="text-muted">
          Pacient: {programare?.pacient?.user?.lastName} {programare?.pacient?.user?.firstName}
        </small>
      </ModalHeader>
      <ModalBody className="py-4">
        {error && (
          <Alert color="danger" className="rounded-pill px-4 shadow-sm mb-4 border-0">
            {error}
          </Alert>
        )}
        <Form>
          <FormGroup className="mb-4">
            <Label for="diagnostic" className="fw-bold text-secondary mb-2">
              Diagnostic
            </Label>
            <Input
              type="textarea"
              id="diagnostic"
              rows={3}
              placeholder="Introduceți diagnosticul..."
              value={diagnostic}
              onChange={e => setDiagnostic(e.target.value)}
              className="rounded-3 border-light shadow-sm"
            />
          </FormGroup>
          <FormGroup className="mb-4">
            <Label for="tratament" className="fw-bold text-secondary mb-2">
              Tratament (Rețetă)
            </Label>
            <Input
              type="textarea"
              id="tratament"
              rows={5}
              placeholder="Introduceți medicamentele și dozele..."
              value={tratament}
              onChange={e => setTratament(e.target.value)}
              className="rounded-3 border-light shadow-sm"
            />
          </FormGroup>
          <FormGroup className="mb-0">
            <Label for="recomandari" className="fw-bold text-secondary mb-2">
              Recomandări suplimentare
            </Label>
            <Input
              type="textarea"
              id="recomandari"
              rows={3}
              placeholder="Alte recomandări pentru pacient..."
              value={recomandari}
              onChange={e => setRecomandari(e.target.value)}
              className="rounded-3 border-light shadow-sm"
            />
          </FormGroup>
        </Form>
      </ModalBody>
      <ModalFooter className="border-0 bg-light-subtle py-3">
        <Button color="secondary" outline onClick={toggle} className="rounded-pill px-4 border-0">
          Anulează
        </Button>
        <Button color="primary" onClick={handleSave} disabled={loading} className="rounded-pill px-4 shadow-sm ms-2">
          {loading ? 'Se salvează...' : 'Salvează Fișa'}
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default ReportFormModal;
