/**
 * MediaFrame is an Open Source streaming media platform in Java 
 * which provides a fast, easy to implement and extremely small applet 
 * that enables to view your audio/video content without having 
 * to rely on external player applications or bulky plug-ins.
 * 
 * Copyright (C) 2004/5 MediaFrame (http://www.mediaframe.org).
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
/************************** MPEG-2 NBC Audio Decoder **************************
 *                                                                           
 * "This software module was originally developed by 
 * AT&T, Dolby Laboratories, Fraunhofer Gesellschaft IIS in the course of 
 * development of the MPEG-2 NBC/MPEG-4 Audio standard ISO/IEC 13818-7, 
 * 14496-1,2 and 3. This software module is an implementation of a part of one or more 
 * MPEG-2 NBC/MPEG-4 Audio tools as specified by the MPEG-2 NBC/MPEG-4 
 * Audio standard. ISO/IEC  gives users of the MPEG-2 NBC/MPEG-4 Audio 
 * standards free license to this software module or modifications thereof for use in 
 * hardware or software products claiming conformance to the MPEG-2 NBC/MPEG-4
 * Audio  standards. Those intending to use this software module in hardware or 
 * software products are advised that this use may infringe existing patents. 
 * The original developer of this software module and his/her company, the subsequent 
 * editors and their companies, and ISO/IEC have no liability for use of this software 
 * module or modifications thereof in an implementation. Copyright is not released for 
 * non MPEG-2 NBC/MPEG-4 Audio conforming products.The original developer
 * retains full right to use the code for his/her  own purpose, assign or donate the 
 * code to a third party and to inhibit third party from using the code for non 
 * MPEG-2 NBC/MPEG-4 Audio conforming products. This copyright notice must
 * be included in all copies or derivative works." 
 * Copyright(c)1996.
 * 
 ******************************************************************************/
package mediaframe.mpeg4.audio.AAC;

import java.io.IOException;

/***********************************************************************************
 * MONOPRED
 * 
 * Contains the core functions for an intra channel (or mono) predictor using a
 * backward adaptive lattice predictor.
 * 
 * init_pred_stat(): initialisation of all predictor parameters monopred():
 * calculation of a predicted value from preceeding (quantised) samples
 * predict(): carry out prediction for all spectral lines predict_reset(): carry
 * out cyclic predictor reset mechanism (long blocks) resp. full reset (short
 * blocks)
 * 
 * Internal Functions: reset_pred_state(): reset the predictor state variables
 * 
 **********************************************************************************/
/* source monopred.c */
public final class Monopred {

	static int GRAD = Constants.PRED_ORDER;
	static float ALPHA;
	static float A;
	static float B;

	/********************************************************************************
	 *** FUNCTION: reset_pred_state()
	 *** 
	 *** reset predictor state variables
	 *** 
	 ********************************************************************************/
	static void reset_pred_state(PRED_STATUS psp) {
		int m, grad;
		grad = GRAD;
		for (m = 0; m <= grad; m++)
			psp.r[m] = 0;
		for (m = 0; m <= grad; m++)
			psp.kor[m] = 0;
		for (m = 0; m <= grad; m++)
			psp.var[m] = 1;
		for (m = 0; m <= grad; m++)
			psp.k[m] = 0;
	}

	/********************************************************************************
	 *** FUNCTION: init_pred_stat()
	 *** 
	 *** initialisation of all predictor parameter
	 *** 
	 ********************************************************************************/
	static void init_pred_stat(PRED_STATUS psp, int grad, float alpha, float a,
			float b) throws IOException {
		/* Test of parameters */

		if ((grad < 0) || (grad > Constants.MAX_PGRAD)) {
			System.out
					.println("\n\n ****** error in routine init_pred_stat ******");
			System.out.println("\nwrong predictor order: " + grad);
			System.out.println("range of allowed values: 0 ... "
					+ Constants.MAX_PGRAD + " (=MAX_PGRAD)\n");
			throw new IOException("Wrong predictor order: " + grad);
		}
		if ((alpha < 0) || (alpha >= 1)) {
			System.out
					.println("\n\n ****** error in routine init_pred_stat ******");
			System.out.println("\nwrong time constant alpha: %e" + alpha);
			System.out.println("range of allowed values: 0 ... 1\n");
			throw new IOException("Wrong time constant alpha: " + alpha);
		}
		if ((a < 0) || (a > 1)) {
			System.out
					.println("\n\n ****** error in routine init_pred_stat ******");
			System.out.println("\nwrong attenuation factor a: " + a);
			System.out.println("range of allowed values: 0 ... 1\n");
			throw new IOException("Wrong attenuation factor a: " + a);
		}
		if ((b < 0) || (b > 1)) {
			System.out
					.println("\n\n ****** error in routine init_pred_stat ******");
			System.out.println("\nwrong attenuation factor b: " + b);
			System.out.println("range of allowed values: 0 ... 1\n");
			throw new IOException("Wrong attenuation factor b: " + b);
		}

		/* Initialisation */
		psp.grad = grad;
		psp.alpha = alpha;
		psp.a = a;
		psp.b = b;

		ALPHA = alpha;
		A = a;
		B = b;

		reset_pred_state(psp);
	}

	/********************************************************************************
	 *** FUNCTION: monopred()
	 *** 
	 *** calculation of a predicted value from preceeding (quantised) samples
	 * using a second order backward adaptive lattice predictor with full LMS
	 * adaption algorithm for calculation of predictor coefficients
	 *** 
	 *** parameters: lqs: last quantised sample psp: pointer to structure with
	 * predictor status
	 *** 
	 *** returns: predicted value
	 ********************************************************************************/

	static float monopred(float lx, PRED_STATUS psp) {
		float pv; /* predicted value */
		float dr1; /* difference in the R-branch */
		float e0, e1; /* "partial" prediction errors (E-branch) */
		float r0, r1; /* content of delay elements */
		float k1, k2; /* predictor coefficients */

		float R[] = psp.r; /* content of delay elements */
		float KOR[] = psp.kor; /* estimates of correlations */
		float VAR[] = psp.var; /* estimates of variances */
		float K[] = psp.k; /* predictor coefficients */

		r0 = R[0];
		r1 = R[1];
		k1 = K[1];
		k2 = K[2];

		/*
		 * E-Branch: Calculate the partial prediction errors using the old
		 * predictor coefficients and the old r-values stored in the registers
		 * in order to reconstruct the predictor status of the previous step
		 * (instead of storage in state variables!)
		 */

		e0 = lx;
		e1 = e0 - k1 * r0;

		/*
		 * Difference in the R-Branch: Calculate the difference in the R-Branch
		 * using the old predictor coefficients and the old partial prediction
		 * errors as calculated above in order to reconstruct the predictor
		 * status of the previous step (instead of storage in state variables!)
		 */

		dr1 = k1 * e0;

		/*
		 * Adaption of variances and correlations for predictor coefficients:
		 * These calculations are based on the predictor status of the previous
		 * step and give the new estimates of variances and correlations used
		 * for the calculations of the new predictor coefficients to be used for
		 * calculating the current predicted value
		 */

		VAR[1] = ALPHA * VAR[1] + (0.5F) * (r0 * r0 + e0 * e0); /* float const */
		KOR[1] = ALPHA * KOR[1] + r0 * e0;
		VAR[2] = ALPHA * VAR[2] + (0.5F) * (r1 * r1 + e1 * e1); /* float const */
		KOR[2] = ALPHA * KOR[2] + r1 * e1;

		/* Summation and delay in the R-Branch => new R-values */

		r1 = A * (r0 - dr1);
		r0 = A * e0;

		/*
		 * Calculation of new predictor coefficients to be used for the
		 * calculation of the current predicted value
		 */
		k1 = (VAR[1] > Constants.MINVAR) ? KOR[1] / VAR[1] * B : 0.0F;
		k2 = (VAR[2] > Constants.MINVAR) ? KOR[2] / VAR[2] * B : 0.0F;

		/* Predicted values (using the new predictor coefficients!) */
		pv = k1 * r0 + k2 * r1;

		K[1] = k1;
		K[2] = k2;
		R[0] = r0;
		R[1] = r1;

		return (pv);
	}

	/********************************************************************************
	 *** FUNCTION: predict()
	 *** 
	 *** carry out prediction for all allowed spectral lines
	 *** 
	 ********************************************************************************/

	static void predict(Info info, int profile, int[] lpflag,
			PRED_STATUS[] psp, float[] prev_quant, float[] coef)
			throws IOException {
		int j, k, b, to, flag0;
		short[] top;
		int lp_flag_index = 0;
		int top_index = 0;

		if (profile != Constants.Main_Profile) {
			if (lpflag[0] != 0) {
				throw new IOException(
						"Prediction isn't allowed in this profile!");
			} else {
				/* prediction calculations not required */
				return;
			}
		}

		if (info.islong) {
			b = 0;
			k = 0;
			top = info.sbk_sfb_top[b];
			flag0 = lpflag[lp_flag_index++];
			for (j = 0; j < Constants.MAX_PRED_SFB; j++) {
				to = top[top_index++];
				if ((flag0 > 0) && (lpflag[lp_flag_index++] > 0)) {
					for (; k < to; k++) {
						coef[k] += monopred(prev_quant[k], psp[k]);
					}
				} else {
					for (; k < to; k++) {
						monopred(prev_quant[k], psp[k]);
					}
				}
			}
			// fltcpy(prev_quant, coef, Constants.LN2); @TODO check this code
			for (int i = 0; i < Constants.LN2; i++) {
				prev_quant[i] = coef[i];
			}
		}
	}

	/********************************************************************************
	 *** FUNCTION: predict_reset()
	 *** 
	 *** carry out cyclic predictor reset mechanism (long blocks) resp. full reset
	 * (short blocks)
	 *** 
	 ********************************************************************************/
	static void predict_reset(Info info, int[] prstflag, PRED_STATUS[][] psp,
			float[][] prev_quant, int firstCh, int lastCh) {
		int j, prstflag0, prstgrp, ch;
		int prstflag_index = 0;

		prstgrp = 0;
		if (info.islong) {
			prstflag0 = prstflag[prstflag_index++];
			if (prstflag0 > 0) {
				for (j = Constants.LEN_PRED_RSTGRP - 1; j > 0; j--) {
					prstgrp |= prstflag[j];
					prstgrp <<= 1;
				}
				prstgrp |= prstflag[0];
				/*
				 * if (AACDecoder.debug[Constants.DEBUG_R]) {
				 * System.out.print("PRST: prstgrp: " + prstgrp +
				 * "  prstbits: "); for (j=Constants.LEN_PRED_RSTGRP-1; j>=0;
				 * j--) { System.out.print(prstflag[j] + " "); }
				 * System.out.println("FIRST: " + firstCh + " LAST " + lastCh);
				 * }
				 */
				if ((prstgrp < 1) || (prstgrp > 30)) {
					System.out.println("ERROR in prediction reset pattern");
					return;
				}

				for (ch = firstCh; ch <= lastCh; ch++) {
					for (j = prstgrp - 1; j < Constants.LN2; j += 30) {
						reset_pred_state(psp[ch][j]);
						prev_quant[ch][j] = 0.0F;
					}
				}
			}
			/* end predictor reset */
		} /* end islong */
		else { /* short blocks */
			/* complete prediction reset in all bins */
			for (ch = firstCh; ch <= lastCh; ch++) {
				for (j = 0; j < Constants.LN2; j++)
					reset_pred_state(psp[ch][j]);
				for (int i = 0; i < Constants.LN2; i++) {
					prev_quant[ch][i] = 0;
				}
			}
		}
	}
}
