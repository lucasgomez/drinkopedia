import React, { Component } from 'react';
import {
  Table,
  Tooltip,
  OverlayTrigger
} from 'react-bootstrap';
import {
  Link
} from 'react-router-dom';


class Welcome extends Component {
  render() {
    return (
      <div class="container">
        <h2>Tout, tout, tout, vous saurez tout sur la bibine</h2>

        <p>Bon, pour être honnête, pas exactement tout, mais ce qui s'en approche le plus au format
        <i>Page Oueb à la Rache</i>. S'il fallait être honnête, ce que je ne suis qu'aux heures où je
        ne mens pas, on dira que cétacé (z'avez compris?) pour l'usage qu'on en fera durant la Fête!</p>

        <p>Le menu ci-dessus vous guidera dans la difficile quête du breuvage parfait au moment adéquat.
        Bon, pour le moment, tant que vous respectez les horaires de la Fête de la Bière [LIEN REQUIS].</p>

        <p>Mu</p>
      </div>
    );
  }
}

export default Welcome;