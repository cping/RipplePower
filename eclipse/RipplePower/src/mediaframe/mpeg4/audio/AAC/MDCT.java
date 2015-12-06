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

/**
 * MDCT
 */
public final class MDCT {

	public static final double d2PI = 6.283185307179586;
	public static final double PI = 3.141592653589795;

	/* MDCT.cpp - Implementation file for fast MDCT transform */
	/*
	 * Note: Defines Transform() and ITransform() for compatibility w/ header
	 * files in previous versions. Replace previous Transfo.c file!
	 */

	/* In-place forward MDCT transform */

	static void Transform(float[] data, int N, int b) {
		MDCT_Transform(data, N, b, 1);
	}

	/* In-place inverse MDCT transform */
	/* Note: 2/N factor ! */
	static void ITransform(float[] data, int N, int b) {
		MDCT_Transform(data, N, b, -1);

	}

	private static float[] FFTarray = null; /* the array for in-place FFT */
	private static int oldN = 0;

	/*****************************
	 * Fast MDCT Code
	 *****************************/
	static void MDCT_Transform(float[] data, int N, int b, int isign) {

		float tempr, tempi, c, s, cold, cfreq, sfreq; /*
													 * temps for pre and post
													 * twiddle
													 */
		float freq = (float) (2 * PI / N);
		float fac;
		int i, n;
		int a = N - b;

		/* Choosing to allocate 2/N factor to Inverse Xform! */
		if (isign == 1) {
			fac = 2f; /* 2 from MDCT inverse to forward */
		} else {
			fac = 2f / (float) N; /* remaining 2/N from 4/N IFFT factor */
		}

		if (N > oldN) {
			if (FFTarray != null) {
				FFTarray = null;
			}
			oldN = N;
		}

		if (FFTarray == null)
			FFTarray = new float[N / 2]; /* holds N/4 complex values */

		/* prepare for recurrence relation in pre-twiddle */
		cfreq = (float) Math.cos(freq);
		sfreq = (float) Math.sin(freq);
		c = (float) Math.cos(freq * 0.125);
		s = (float) Math.sin(freq * 0.125);

		for (i = 0; i < (N / 4); i++) {
			/* calculate real and imaginary parts of g(n) or G(p) */
			if (isign == 1) { /* Forward Transform */
				n = N / 2 - 1 - 2 * i;
				if (i < (b / 4)) {
					/* use second form of e(n) for n = N / 2 - 1 - 2i */
					tempr = data[a / 2 + n] + data[N + a / 2 - 1 - n];
				} else {
					/* use first form of e(n) for n = N / 2 - 1 - 2i */
					tempr = data[a / 2 + n] - data[a / 2 - 1 - n];
				}
				n = 2 * i;
				if (i < (a / 4)) {
					tempi = data[a / 2 + n] - data[a / 2 - 1 - n]; /*
																	 * use first
																	 * form of
																	 * e(n) for
																	 * n = N / 2
																	 * - 1 - 2i
																	 */
				} else {
					tempi = data[a / 2 + n] + data[N + a / 2 - 1 - n]; /*
																		 * use
																		 * second
																		 * form
																		 * of
																		 * e(n)
																		 * for n
																		 * = N /
																		 * 2 - 1
																		 * - 2i
																		 * i
																		 */
				}
			} else { /* Inverse Transform */
				tempr = -data[2 * i];
				tempi = data[N / 2 - 1 - 2 * i];
			}

			/* calculate pre-twiddled FFT input */

			FFTarray[2 * i] = tempr * c + isign * tempi * s;
			FFTarray[2 * i + 1] = tempi * c - isign * tempr * s;

			/* use recurrence to prepare cosine and sine for next value of i */
			cold = c;
			c = c * cfreq - s * sfreq;
			s = s * cfreq + cold * sfreq;
		}

		/* Perform in-place complex FFT (or IFFT) of length N/4 */
		/*
		 * Note: FFT has physics (opposite) sign convention and doesn't do 1/N
		 * factor
		 */
		CompFFT(FFTarray, N / 4, -isign);

		/* prepare for recurrence relations in post-twiddle */
		c = (float) Math.cos(freq * 0.125);
		s = (float) Math.sin(freq * 0.125);

		/* post-twiddle FFT output and then get output data */
		for (i = 0; i < (N / 4); i++) {

			/* get post-twiddled FFT output */
			/* Note: fac allocates 4/N factor from IFFT to forward and inverse */
			tempr = fac
					* (FFTarray[2 * i] * c + isign * FFTarray[2 * i + 1] * s);
			tempi = fac
					* (FFTarray[2 * i + 1] * c - isign * FFTarray[2 * i] * s);

			/* fill in output values */
			if (isign == 1) { /* Forward Transform */
				data[2 * i] = -tempr; /* first half even */
				data[N / 2 - 1 - 2 * i] = tempi; /* first half odd */
				data[N / 2 + 2 * i] = -tempi; /* second half even */
				data[N - 1 - 2 * i] = tempr; /* second half odd */
			} else { /* Inverse Transform */
				data[N / 2 + a / 2 - 1 - 2 * i] = tempr;
				if (i < (b / 4)) {
					data[N / 2 + a / 2 + 2 * i] = tempr;
				} else {
					data[2 * i - b / 2] = -tempr;
				}
				data[a / 2 + 2 * i] = tempi;
				if (i < (a / 4)) {
					data[a / 2 - 1 - 2 * i] = -tempi;
				} else {
					data[a / 2 + N - 1 - 2 * i] = tempi;
				}
			}

			/* use recurrence to prepare cosine and sine for next value of i */
			cold = c;
			c = c * cfreq - s * sfreq;
			s = s * cfreq + cold * sfreq;
		}

		/* DeleteFloat (FFTarray); */
	}

	/*
	 * ----------------------------- (Very) Slow MDCT Code
	 * ----------------------------- static void SMDCT_Transform(float[] data,
	 * int N, int b, int isign){ // Very slow implementation for brute force
	 * testing // Note: 2/N factor allocated to forward Xform
	 * 
	 * double phi = 2d*PI/(double)N; double no = 0.5*(b+1); double fac, temp;
	 * double[] outData; int i,j; int a = N-b;
	 * 
	 * outData = new double[N];
	 * 
	 * fac = 2d/(double)N;
	 * 
	 * if (isign==1) { // Forward for (i=0;i<N;i++) { temp=0; for (j=0;j<N;j++)
	 * { temp += data[j]*Math.cos(phi*(i+0.5)*(j+no)); } outData[i] = fac*temp;
	 * } } else { // Inverse for (j=0;j<N;j++) { temp=0; for (i=0;i<N;i++) {
	 * temp += data[i]*Math.cos(phi*(i+0.5)*(j+no)); } outData[j] = temp; } }
	 * for (i=0;i<N;i++) { data[i] = (float)outData[i]; } outData = null; }
	 */

	private static int oldp1 = 0, oldq1 = 0;
	private static float[][] intermed = null;

	static void CompFFT(float[] data, int nn, int isign) {

		int i, j, k, kk;
		int p1, q1;
		int m, n, logq1;
		float ar, ai;
		float d2pn;
		float ca, sa, curcos, cursin, oldcos, oldsin;
		float ca1, sa1, curcos1, cursin1, oldcos1, oldsin1;

		/*
		 * Factorize n; n = p1*q1 where q1 is a power of 2. For n = 1152, p1 =
		 * 9, q1 = 128.
		 */

		n = nn;
		logq1 = 0;

		for (;;) {
			m = n >> 1; /* shift right by one */
			if ((m << 1) == n) {
				logq1++;
				n = m;
			} else {
				break;
			}
		}

		p1 = n;
		q1 = 1;
		q1 <<= logq1;

		d2pn = (float) (d2PI / nn);

		if ((oldp1 < p1) || (oldq1 < q1)) {
			if (intermed != null) {
				intermed = null;
			}
			if (oldp1 < p1)
				oldp1 = p1;
			if (oldq1 < q1)
				oldq1 = q1;
		}

		if (intermed == null)
			intermed = new float[oldp1][2 * oldq1];

		/* Sort the p1 sequences */

		for (i = 0; i < p1; i++) {
			for (j = 0; j < q1; j++) {
				intermed[i][2 * j] = data[2 * (p1 * j + i)];
				intermed[i][2 * j + 1] = data[2 * (p1 * j + i) + 1];
			}
		}

		/* compute the power of two fft of the p1 sequences of length q1 */

		for (i = 0; i < p1; i++) {
			/* Forward FFT in place for n complex items */
			FFT(intermed[i], q1, isign);
		}

		/* combine the FFT results into one seqquence of length N */

		ca1 = (float) Math.cos(d2pn);
		sa1 = (float) Math.sin(d2pn);
		curcos1 = 1;
		cursin1 = 0;

		for (k = 0; k < nn; k++) {
			data[2 * k] = 0;
			data[2 * k + 1] = 0;
			kk = k % q1;

			ca = curcos1;
			sa = cursin1;
			curcos = 1;
			cursin = 0;

			for (j = 0; j < p1; j++) {
				ar = curcos;
				ai = isign * cursin;
				data[2 * k] += intermed[j][2 * kk] * ar
						- intermed[j][2 * kk + 1] * ai;
				data[2 * k + 1] += intermed[j][2 * kk] * ai
						+ intermed[j][2 * kk + 1] * ar;

				oldcos = curcos;
				oldsin = cursin;
				curcos = oldcos * ca - oldsin * sa;
				cursin = oldcos * sa + oldsin * ca;
			}
			oldcos1 = curcos1;
			oldsin1 = cursin1;
			curcos1 = oldcos1 * ca1 - oldsin1 * sa1;
			cursin1 = oldcos1 * sa1 + oldsin1 * ca1;
		}
	}

	static void FFT(float[] data, int nn, int isign) {
		/*
		 * Varient of Numerical Recipes code from off the internet. It takes nn
		 * interleaved complex input data samples in the array data and returns
		 * nn interleaved complex data samples in place where the output is the
		 * FFT of input if isign==1 and it is nn times the IFFT of the input if
		 * isign==-1. (Note: it doesn't renormalize by 1/N when doing the
		 * inverse transform!!!) (Note: this follows physicists convention of
		 * +i, not EE of -j in forward transform!!!!)
		 */

		/*
		 * Press, Flannery, Teukolsky, Vettering "Numerical Recipes in C" tuned
		 * up ; Code works only when nn is a power of 2
		 */

		int n, mmax, m, j, i;
		double theta;
		float wtemp, wr, wpr, wpi, wi, wpin;
		float tempr, tempi, datar, datai;
		float data1r, data1i, tmp;

		n = nn * 2;

		/* bit reversal */

		j = 0;
		for (i = 0; i < n; i += 2) {
			if (j > i) { /* could use j>i+1 to help compiler analysis */
				// SWAP (data [j], data [i]);
				tmp = data[j];
				data[j] = data[i];
				data[i] = tmp;
				// SWAP (data [j + 1], data [i + 1]);
				tmp = data[j + 1];
				data[j + 1] = data[i + 1];
				data[i + 1] = tmp;
			}
			m = nn;
			while ((m >= 2) && (j >= m)) {
				j -= m;
				m >>= 1;
			}
			j += m;
		}

		theta = 3.141592653589795 * 0.5;
		if (isign < 0)
			theta = -theta;
		wpin = 0; /* sin(+-PI) */
		for (mmax = 2; n > mmax; mmax *= 2) {
			wpi = wpin;
			wpin = (float) (Math.sin(theta));
			wpr = 1 - wpin * wpin - wpin * wpin; /* cos(theta*2) */
			theta *= .5;
			wr = 1;
			wi = 0;
			for (m = 0; m < mmax; m += 2) {
				j = m + mmax;
				tempr = (float) wr * (data1r = data[j]);
				tempi = (float) wi * (data1i = data[j + 1]);
				for (i = m; i < n - mmax * 2; i += mmax * 2) {
					/*
					 * mixed precision not significantly more accurate here; if
					 * removing float casts, tempr and tempi should be double
					 */
					tempr -= tempi;
					tempi = (float) wr * data1i + (float) wi * data1r;
					/* don't expect compiler to analyze j > i + 1 */
					data1r = data[j + mmax * 2];
					data1i = data[j + mmax * 2 + 1];
					data[i] = (datar = data[i]) + tempr;
					data[i + 1] = (datai = data[i + 1]) + tempi;
					data[j] = datar - tempr;
					data[j + 1] = datai - tempi;
					tempr = (float) wr * data1r;
					tempi = (float) wi * data1i;
					j += mmax * 2;
				}
				tempr -= tempi;
				tempi = (float) wr * data1i + (float) wi * data1r;
				data[i] = (datar = data[i]) + tempr;
				data[i + 1] = (datai = data[i + 1]) + tempi;
				data[j] = datar - tempr;
				data[j + 1] = datai - tempi;
				wr = (wtemp = wr) * wpr - wi * wpi;
				wi = wtemp * wpi + wi * wpr;
			}
		}
	}
}
