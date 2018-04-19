import React, { Component } from 'react';
import {
  Row,
  Label
} from 'react-bootstrap';
//import FontAwesome from '../react-fontawesome';

class RatedLabel extends Component {

  render() {
    const {
      name,
      strength
    } = this.props;
    return (
      <Row><Label>{name}</Label><div>{this.displayStars(strength)}</div></Row>
    );

  }

  displayStars(strength) {

    const emptyStar = (<i className="fa fa-beer" style="color:lightgrey"></i>);
    const checkedStar = (<i className="fa fa-beer checked"></i>);
    const uncheckedStar = (<i className="fa fa-beer"></i>);
    //const uncheckedStar = (<FontAwesome name="fa fa-beer"></FontAwesome>);

    let rating = [];
    let i = 0;
    if (strength === 0) {
      for (i = 0; i < 5; i++) {
        rating.push("-");
        // rating.push(emptyStar);
      }
    } else {
      for (i = 0; i < strength; i++) {
        rating.push("X");
        // rating.push(checkedStar);
      }
      for (i = 0; i < 5-strength; i++) {
        rating.push("O");
        // rating.push(uncheckedStar);
      }
    }
    // return rating.map(
    //         (star) =>
    //           {rating}
    //       );
    if (strength > 0)
      return strength+"/5";
    else
      return "N/A";
  }
}

export default RatedLabel;
