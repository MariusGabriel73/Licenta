import React from 'react';
import { Translate } from 'react-jhipster';

import { NavItem, NavLink, NavbarBrand } from 'reactstrap';
import { NavLink as Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

export const BrandIcon = props => (
  <div {...props} className="brand-icon">
    <img src="content/images/logo-jhipster.png" alt="Logo" />
  </div>
);

export const Brand = () => (
  <NavbarBrand tag={Link} to="/" className="brand-logo">
    <BrandIcon />
    <span className="brand-title">
      <Translate contentKey="global.title">Medicalsystem</Translate>
    </span>
  </NavbarBrand>
);

export const Home = () => (
  <NavItem>
    <NavLink tag={Link} to="/" className="d-flex align-items-center">
      <FontAwesomeIcon icon="home" />
      <span>
        <Translate contentKey="global.menu.home">Home</Translate>
      </span>
    </NavLink>
  </NavItem>
);

export const Prescriptions = () => (
  <NavItem>
    <NavLink tag={Link} to="/prescriptions" className="d-flex align-items-center">
      <FontAwesomeIcon icon="stethoscope" />
      <span>Rețete</span>
    </NavLink>
  </NavItem>
);

export const MedicAgenda = () => (
  <NavItem>
    <NavLink tag={Link} to="/medic" className="d-flex align-items-center">
      <FontAwesomeIcon icon="calendar-alt" />
      <span>Agendă</span>
    </NavLink>
  </NavItem>
);

export const PatientAppointments = () => (
  <NavItem>
    <NavLink tag={Link} to="/pacient" className="d-flex align-items-center">
      <FontAwesomeIcon icon="calendar-check" />
      <span>Programări</span>
    </NavLink>
  </NavItem>
);
export const MedicalAnalysis = () => (
  <NavItem>
    <NavLink tag={Link} to="/analize-medicale" className="d-flex align-items-center">
      <FontAwesomeIcon icon="flask" />
      <span>Analize</span>
    </NavLink>
  </NavItem>
);
