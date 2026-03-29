import './footer.scss';

import React from 'react';
import { Translate } from 'react-jhipster';
import { Col, Row } from 'reactstrap';

const Footer = () => (
  <div className="footer text-center mt-5 mb-4 pt-4 border-top">
    <p className="text-muted small mb-0" style={{ fontWeight: 500 }}>
      &copy; {new Date().getFullYear()} Medical System. Toate drepturile rezervate.
    </p>
  </div>
);

export default Footer;
