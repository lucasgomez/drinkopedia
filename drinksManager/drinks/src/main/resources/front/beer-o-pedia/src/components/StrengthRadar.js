import React, { Component } from 'react';
import { ReactstrapRadio } from "reactstrap-formik";
import { Field } from 'formik';

class StrengthRadar extends Component {
  componentDidMount() {
      this.updateCanvas();
  }

  updateCanvas() {
    const ctx = this.refs.canvas.getContext('2d');
		this.drawTarget(ctx);
  	this.drawRuler(ctx);
    this.drawQuadrilater(ctx,
      this.props.bitterness,
      this.props.hopping,
      this.props.sweetness,
      this.props.sourness);
		this.writeLabels(ctx,
      'Amertume',
      'Houblonnage',
      'Douceur',
      'Acidité');
  }

  writeLabels(ctx, label1, label2, label3, label4) {
		ctx.font = '11px sans-serif';
		ctx.textBaseline = 'middle';
		ctx.textAlign = 'center';

		//Normal orientation
		ctx.strokeText(label1, 75, 7);
		ctx.strokeText(label3, 75, 143);

		//Rotated texts
		ctx.save();
		ctx.translate(0,150);
		ctx.rotate(-Math.PI/2);
		ctx.strokeText(label2, 75, 143);
		ctx.strokeText(label4, 75, 7);

		ctx.restore();
  }

  drawQuadrilater(ctx, value1, value2, value3, value4) {
  	ctx.beginPath();
		ctx.fillStyle = 'rgb(200, 0, 0, 0.75)';
		ctx.moveTo(75,75-value1*20);
		ctx.lineTo(75+value2*20,75);
		ctx.lineTo(75,75+value3*20);
		ctx.lineTo(75-value4*20,75);
		ctx.lineTo(75,75-value1*20);
		ctx.fill();
  }

  drawTarget(ctx) {
		//Cercles concentriques
		ctx.beginPath();
		ctx.fillStyle = 'rgb(150, 200, 230)';
		ctx.arc(75, 75, 60, 0, Math.PI * 2, true);
		ctx.fill();

		ctx.beginPath();
		ctx.fillStyle = 'rgb(175, 225, 240)';
		ctx.moveTo(115, 75);
		ctx.arc(75, 75, 40, 0, Math.PI * 2, true);
		ctx.fill();

		ctx.beginPath();
		ctx.fillStyle = 'rgb(225, 245, 250)';
		ctx.moveTo(95, 75);
		ctx.arc(75, 75, 20, 0, Math.PI * 2, true);
		ctx.fill();
  }

  drawRuler(ctx) {
		ctx.beginPath();
		//Visée
		ctx.moveTo(75, 15);
		ctx.lineTo(75, 135);
		ctx.moveTo(15, 75);
		ctx.lineTo(135, 75);
		ctx.stroke();
  }

  render() {
      return (
          <canvas ref="canvas" width={150} height={150}/>
      );
  }
}

export default StrengthRadar;
