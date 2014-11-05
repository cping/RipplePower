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
 * Tns
 */
public class Tns {

	private Huffman huffman;

	private BitStream audio_stream;

	private AACDecoder decoder;

	private Config config;

	public Tns(AACDecoder decoder) {
		super();
		this.decoder = decoder;
		this.huffman = decoder.getHuffman();
		this.audio_stream = decoder.getAudio_Stream();
		this.config = decoder.getConfig();

	}

	/* Decoder transmitted coefficients for one TNS filter */
	void tns_decode_coef(int order, int coef_res, short[] coef, float[] a) {
		int i, m;
		float iqfac, iqfac_m;
		float[] tmp = new float[Constants.TNS_MAX_ORDER + 1];
		float[] b = new float[Constants.TNS_MAX_ORDER + 1];

		/* Inverse quantization */
		iqfac = (float) (((1 << (coef_res - 1)) - 0.5) / (Constants.C_PI / 2.0f));
		iqfac_m = (float) (((1 << (coef_res - 1)) + 0.5) / (Constants.C_PI / 2.0f));
		for (i = 0; i < order; i++) {
			tmp[i + 1] = (float) Math.sin(coef[i]
					/ ((coef[i] >= 0) ? iqfac : iqfac_m));
		}
		/*
		 * Conversion to LPC coefficients Markel and Gray, pg. 95
		 */
		a[0] = 1;
		for (m = 1; m <= order; m++) {
			b[0] = a[0];
			for (i = 1; i < m; i++) {
				b[i] = a[i] + tmp[m] * a[m - i];
			}
			b[m] = tmp[m];
			for (i = 0; i <= m; i++) {
				a[i] = b[i];
			}
		}

	}

	/* apply the TNS filter */
	void tns_ar_filter(float[] spec, int spec_index, int size, int inc,
			float[] lpc, int order) {
		/*
		 * - Simple all-pole filter of order "order" defined by y(n) = x(n) -
		 * a(2)*y(n-1) - ... - a(order+1)*y(n-order)
		 *  - The state variables of the filter are initialized to zero every
		 * time
		 *  - The output data is written over the input data ("in-place
		 * operation")
		 *  - An input vector of "size" samples is processed and the index
		 * increment to the next data sample is given by "inc"
		 */
		int i, j;
		float y;
		float[] state = new float[Constants.TNS_MAX_ORDER];

		for (i = 0; i < order; i++) {
			state[i] = 0;
		}

		if (inc == -1) {
			spec_index += size - 1;
		}

		for (i = 0; i < size; i++) {
			y = spec[spec_index];
			for (j = 0; j < order; j++)
				y -= lpc[j + 1] * state[j];
			for (j = order - 1; j > 0; j--)
				state[j] = state[j - 1];
			state[0] = y;
			spec[spec_index] = y;
			spec_index += inc;
		}
	}

	/* TNS decoding for one channel and frame */
	void tns_decode_subblock(float[] spec, int spec_index, int nbands,
			short[] sfb_top, boolean islong, TNSinfo tns_info) {
		int f, m, start, stop, size, inc;
		int n_filt, coef_res, order, direction;
		short[] coef;
		float[] lpc = new float[Constants.TNS_MAX_ORDER + 1];
		TNSfilt filt;

		n_filt = tns_info.n_filt;
		for (f = 0; f < n_filt; f++) {
			coef_res = tns_info.coef_res;
			filt = tns_info.filt[f];
			order = filt.order;
			direction = filt.direction;
			coef = filt.coef;
			start = filt.start_band;
			stop = filt.stop_band;

			m = config.tns_max_order(islong);
			if (order > m) {
				System.out.println("Error in tns max order: " + order + " "
						+ config.tns_max_order(islong));
				order = m;
			}
			if (order == 0)
				continue;

			tns_decode_coef(order, coef_res, coef, lpc);

			int value = config.tns_max_bands(islong);
			if (value < start) {
				start = value;
			}
			start = (((start) > 0) ? sfb_top[(start) - 1] : 0);

			value = config.tns_max_bands(islong);
			if (value < stop) {
				stop = value;
			}
			stop = (((stop) > 0) ? sfb_top[(stop) - 1] : 0);
			if ((size = stop - start) <= 0)
				continue;

			if (direction > 0) {
				inc = -1;
			} else {
				inc = 1;
			}

			tns_ar_filter(spec, start + spec_index, size, inc, lpc, order);
		}
	}

	void print_tns(TNSinfo tns_info) {
		int f, t;
		String s = "TNS>> ";

		System.out.println(s + " n_filt: " + tns_info.n_filt);
		if (tns_info.n_filt > 0)
			System.out.println(s + " res   : " + tns_info.coef_res);
		for (f = 0; f < tns_info.n_filt; f++) {

			System.out.print(s + " filt " + f + "["
					+ tns_info.filt[f].start_band + " "
					+ tns_info.filt[f].stop_band + "] o="
					+ tns_info.filt[f].order);

			if (tns_info.filt[f].order > 0) {
				System.out.print(" d=" + tns_info.filt[f].direction + " | ");

				for (t = 0; t < tns_info.filt[f].order; t++)
					System.out.print(tns_info.filt[f].coef[t] + " ");

			}
			System.out.println();
		}
		System.out.println(s + " ------------\n");
	}

	public BitStream getAudio_stream() {
		return audio_stream;
	}

	public void setAudio_stream(BitStream audio_stream) {
		this.audio_stream = audio_stream;
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	public AACDecoder getDecoder() {
		return decoder;
	}

	public void setDecoder(AACDecoder decoder) {
		this.decoder = decoder;
	}

	public Huffman getHuffman() {
		return huffman;
	}

	public void setHuffman(Huffman huffman) {
		this.huffman = huffman;
	}
}
