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
package mediaframe.mpeg4.video;

import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;

import org.ripple.power.sound.IMpeg4;

//import mediaframe.mpeg4.MPEG4;

/**
 * The <code>MPEG4Decoder</code>
 * 
 * @author Konstantin Belous
 */
public final class MPEG4Decoder implements Runnable {

	/** The visual object sequence start code. */
	public final static int VISUAL_OBJECT_SEQUENCE_START_CODE = 0xB0;

	/** The visual object start code. */
	public final static int VISUAL_OBJECT_START_CODE = 0xB5;

	/** The video object start code. */
	public final static int VIDEO_OBJECT_START_CODE = 0x1F;

	/** The video object layer start code. */
	public final static int VIDEO_OBJECT_LAYER_START_CODE = 0x2F;

	/** The group of vop start code value. */
	public final static int GROUP_VOP_START_CODE = 0xB3;

	/** The vop start code value. */
	public final static int VOP_START_CODE = 0xB6;

	/** The resync marker value. */
	public final static int RESYNC_MARKER = 1;

	/** 'Intra coded' VOP coding type. */
	public final static int I_VOP = 0;

	/** 'Predictive coded' VOP coding type. */
	public final static int P_VOP = 1;

	/** 'Bidirectionally-predictive coded' VOP coding type. */
	public final static int B_VOP = 3;

	/** 'Sprite' VOP coding type. */
	public final static int S_VOP = 4;

	/** The extended PAR pixel aspect ratio code. */
	public final static int EXTENDED_ASPECT_RATIO = 15;

	/** The 'rectangular' shape type of a video object layer. */
	public final static byte RECTANGULAR_SHAPE = 0;

	/** The 'binary' shape type of a video object layer. */
	public final static byte BINARY_SHAPE = 1;

	/** The 'binary only' shape type of a video object layer. */
	public final static byte BINARY_ONLY_SHAPE = 2;

	/** The 'grayscale' shape type of a video object layer. */
	public final static byte GRAYSCALE_SHAPE = 3;

	/** The 'sprite not used' sprite coding mode. */
	public final static byte NOT_USED_SPRITE = 0;

	/** The 'static (Basic/Low Latency)' sprite coding mode. */
	public final static byte STATIC_SPRITE = 1;

	/** The 'GMC (Global Motion Compensation)' sprite coding mode. */
	public final static byte GMC_SPRITE = 2;

	/** The 'Stop' transmit mode of the sprite object. */
	public final static byte STOP_TRANSMIT_MODE = 0;

	/** The 'Piece' transmit mode of the sprite object. */
	public final static byte PIECE_TRANSMIT_MODE = 1;

	/** The 'Update' transmit mode of the sprite object. */
	public final static byte UPDATE_TRANSMIT_MODE = 2;

	/** The 'Pause' transmit mode of the sprite object. */
	public final static byte PAUSE_TRANSMIT_MODE = 3;

	/** The 'Direct' motion mode. */
	public final static byte DIRECT_MOTION_MODE = 2;

	/** The 'Interpolate' motion mode. */
	public final static byte INTERPOLATE_MOTION_MODE = 2;

	/** The 'Backward' motion mode. */
	public final static byte BACKWARD_MOTION_MODE = 3;

	/** The 'Forward' motion mode. */
	public final static byte FORWARD_MOTION_MODE = 4;

	/** The default matrix for intra blocks. */
	public final static int[] DEFAULT_INTRA_QUANT_MAT = { 8, 17, 18, 19, 21,
			23, 25, 27, 17, 18, 19, 21, 23, 25, 27, 28, 20, 21, 22, 23, 24, 26,
			28, 30, 21, 22, 23, 24, 26, 28, 30, 32, 22, 23, 24, 26, 28, 30, 32,
			35, 23, 24, 26, 28, 30, 32, 35, 38, 25, 26, 28, 30, 32, 35, 38, 41,
			27, 28, 30, 32, 35, 38, 41, 45 };

	/** The default matrix for non-intra blocks. */
	public final static int[] DEFAULT_NON_INTRA_QUANT_MAT = { 16, 17, 18, 19,
			20, 21, 22, 23, 17, 18, 19, 20, 21, 22, 23, 24, 18, 19, 20, 21, 22,
			23, 24, 25, 19, 20, 21, 22, 23, 24, 26, 27, 20, 21, 22, 23, 25, 26,
			27, 28, 21, 22, 23, 24, 26, 27, 28, 30, 22, 23, 24, 26, 27, 28, 30,
			31, 23, 24, 25, 27, 28, 30, 31, 33 };

	/** The Alternate-Horizontal scan pattern. */
	public static int[] ALTERNATE_HORIZONTAL_SCAN_TABLE = { 0, 1, 2, 3, 8, 9,
			16, 17, 10, 11, 4, 5, 6, 7, 15, 14, 13, 12, 19, 18, 24, 25, 32, 33,
			26, 27, 20, 21, 22, 23, 28, 29, 30, 31, 34, 35, 40, 41, 48, 49, 42,
			43, 36, 37, 38, 39, 44, 45, 46, 47, 50, 51, 56, 57, 58, 59, 52, 53,
			54, 55, 60, 61, 62, 63, };

	/** The Alternate-Vertical scan pattern. */
	public static int[] ALTERNATE_VERTICAL_SCAN_TABLE = { 0, 8, 16, 24, 1, 9,
			2, 10, 17, 25, 32, 40, 48, 56, 57, 49, 41, 33, 26, 18, 3, 11, 4,
			12, 19, 27, 34, 42, 50, 58, 35, 43, 51, 59, 20, 28, 5, 13, 6, 14,
			21, 29, 36, 44, 52, 60, 37, 45, 53, 61, 22, 30, 7, 15, 23, 31, 38,
			46, 54, 62, 39, 47, 55, 63, };

	/** The Zigzag scan pattern. */
	public static int[] ZIGZAG_SCAN_TABLE = { 0, 1, 8, 16, 9, 2, 3, 10, 17, 24,
			32, 25, 18, 11, 4, 5, 12, 19, 26, 33, 40, 48, 41, 34, 27, 20, 13,
			6, 7, 14, 21, 28, 35, 42, 49, 56, 57, 50, 43, 36, 29, 22, 15, 23,
			30, 37, 44, 51, 58, 59, 52, 45, 38, 31, 39, 46, 53, 60, 61, 54, 47,
			55, 62, 63, };

	/** The LMAX table of intra macroblocks. */
	public final static int[][][] INTRA_LMAX_TAB = {
			{ { 0, 27 }, { 1, 10 }, { 2, 5 }, { 3, 4 }, { 7, 3 }, { 9, 2 },
					{ 14, 1 } }, { { 0, 8 }, { 1, 3 }, { 6, 2 }, { 20, 1 } } };

	/** The LMAX table of inter macroblocks. */
	public final static int[][][] INTER_LMAX_TAB = {
			{ { 0, 12 }, { 1, 6 }, { 2, 4 }, { 6, 3 }, { 10, 2 }, { 26, 1 } },
			{ { 0, 3 }, { 1, 2 }, { 40, 1 } } };

	/** The RMAX table of intra macroblocks. */
	public final static int[][][] INTRA_RMAX_TAB = {
			{ { 1, 14 }, { 2, 9 }, { 3, 7 }, { 4, 3 }, { 5, 2 }, { 10, 1 },
					{ 27, 0 } }, { { 1, 20 }, { 2, 6 }, { 3, 1 }, { 8, 0 } } };

	/** The RMAX table of inter macroblocks. */
	public final static int[][][] INTER_RMAX_TAB = {
			{ { 1, 26 }, { 2, 10 }, { 3, 6 }, { 4, 2 }, { 6, 1 }, { 12, 0 } },
			{ { 1, 40 }, { 2, 1 }, { 3, 0 } } };

	public static final int[] aux_comp_count = { 1, 1, 2, 2, 3, 1, 2, 1, 1, 2,
			3, 2, 3, 1, 1, 1 };

	/** The input MPEG4 video bitstream. */
	private BitStream videoStream = null;

	/** The VLC (Huffman) decoder. */
	private Huffman huffman = null;

	/** The thread of the video decoder. */
	private volatile Thread videoThread = null;

	/** <tt>True</tt>, if the decoder starts to decode the video stream. */

	/** The fps value of the video stream. */
	private double video_rate;

	/** The size in bytes of the video stream. */
	private int video_size;

	/** The reference to the applet's object. */
	private IMpeg4 mpeg4;

	/**
	 * Constructs an <code>MPEG4Decoder</code> object.
	 * 
	 * @param mpeg4
	 *            the reference to the mpeg4's object.
	 * @param bitstream
	 *            the input MPEG4 video bitstream.
	 */
	public MPEG4Decoder(IMpeg4 mpeg4, BitStream videoStream, int width,
			int height, double video_rate, int video_size) {
		super();
		this.videoStream = videoStream;
		this.huffman = new Huffman(videoStream);
		this.mpeg4 = mpeg4;
		this.video_rate = video_rate;
		this.video_size = video_size;
		this.video_object_layer_width = (short) width;
		this.video_object_layer_height = (short) height;

		this.vop_time_increment_resolution = (int) video_rate;
		while (((double) vop_time_increment_resolution / video_rate)
				- (int) ((double) vop_time_increment_resolution / video_rate) > 0.000000001d) {
			vop_time_increment_resolution++;
		}
		vop_time_increment_length = 31;
		while (((vop_time_increment_resolution >>> vop_time_increment_length) == 0)
				&& (vop_time_increment_length > 0)) {
			vop_time_increment_length--;
		}
		vop_time_increment_length++;
		vop_id_length = vop_time_increment_length + 3;
		vop_id_length = vop_id_length > 15 ? 15 : vop_id_length;

		videoThread = new Thread(this, "Video Thread");
		videoThread.start();
	}

	/**
	 * Stops the playing of the video.
	 */
	public synchronized void stop() {
		if (videoThread != null) {
			Thread workThread = videoThread;
			videoThread = null;
			workThread.interrupt();
			try {
				workThread.join(2000);
			} catch (Exception ex) {
			}
		}
	}

	public void run() {
		try {
			while ((videoThread != null) && (decodeStream() == true)) {
			}
		} catch (Exception ex) {
		} finally {
			videoThread = null;
		}
	}

	private boolean printed_video_info = false;

	private int iVideo_rate;

	private int duration;

	private boolean decodeStream() {
		try {
			int start_code = videoStream.get_next_start_code();
			switch (start_code) {
			case VISUAL_OBJECT_SEQUENCE_START_CODE:
				decode_VisualObjectSequence();
				break;
			case VISUAL_OBJECT_START_CODE:
				decode_VisualObject();
				break;
			case GROUP_VOP_START_CODE:
				decode_Group_of_VideoObjectPlane();
				break;
			case VOP_START_CODE:
				if (!printed_video_info) {
					video_rate += 0.005d; // try to round the video_rate to
											// the nearest double value
					iVideo_rate = (int) video_rate;
					duration = (int) Math.round(mpeg4.getVideoLength() / 1000d);
					printed_video_info = true;
				}
				decode_VideoObjectPlane();
				break;
			default:
				if (start_code <= VIDEO_OBJECT_START_CODE) {
					break;
				}
				if (start_code <= VIDEO_OBJECT_LAYER_START_CODE) {
					video_object_layer_id = (byte) (start_code & 0x0f);
					decode_VideoObjectLayer();
					break;
				}
				// System.out.println("Unknown start code: " +
				// Integer.toHexString(start_code));
			}
			return true;
		} catch (EOFException eofex) {
			mpeg4.playerend();
			return false;
		} catch (InterruptedIOException ioe) {
			return false;
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return true;
		}
	}

	/** The profile and level indication of the visual stream. */
	private short profile_and_level_indication = 1;

	private void decode_VisualObjectSequence() throws IOException {
		profile_and_level_indication = (short) videoStream.next_bits(8);
		/*
		 * System.out.println("VisualObjectSequence()");
		 * System.out.println("Profile And Level Indication = " +
		 * profile_and_level_indication);
		 */
	}

	private boolean is_visual_object_identifier;

	private byte visual_object_type;

	private boolean video_signal_type;

	private boolean colour_description;

	private void decode_VisualObject() throws IOException {
		is_visual_object_identifier = videoStream.next_bit();
		if (is_visual_object_identifier) {
			videoStream.next_bits(4);
			videoStream.next_bits(3);
		}
		visual_object_type = (byte) videoStream.next_bits(4);
		if (visual_object_type == 1) {
			video_signal_type = videoStream.next_bit();
			if (video_signal_type) {
				videoStream.next_bits(3);
				videoStream.next_bit();
				colour_description = videoStream.next_bit();
				if (colour_description) {
					videoStream.next_bits(8);
					videoStream.next_bits(8);
					videoStream.next_bits(8);
				}
			}
		}
	}

	private byte video_object_layer_id;

	private boolean short_video_header = false;

	private boolean random_accessible_vol;

	private short video_object_type_indication;

	private boolean is_object_layer_identifier;

	private byte video_object_layer_verid = 1;

	private byte video_object_layer_priority;

	/** The value of pixel aspect ratio. */
	private byte aspect_ratio_info;

	private short par_width;

	private short par_height;

	private boolean vol_control_parameters;

	private byte chroma_format;

	private boolean low_delay;

	private boolean vbv_parameters;

	private short first_half_bit_rate;

	private short latter_half_bit_rate;

	private short first_half_vbv_buffer_size;

	private byte latter_half_vbv_buffer_size;

	private short first_half_vbv_occupancy;

	private short latter_half_vbv_occupancy;

	private byte video_object_layer_shape = RECTANGULAR_SHAPE;

	private byte video_object_layer_shape_extension;

	private int vop_time_increment_resolution;

	private int vop_time_increment_length;

	private int vop_id_length = 8;

	private boolean fixed_vop_rate = false;

	private int fixed_vop_time_increment;

	private short video_object_layer_width;

	private short video_object_layer_height;

	private boolean interlaced = false;

	private boolean obmc_disable = true;

	private byte sprite_enable = 0;

	private byte no_of_sprite_warping_points;

	private boolean sadct_disable = true;

	private boolean low_latency_sprite_enable;

	private boolean not_8_bit = false;

	/** The number of bits used to represent quantiser parameters. */
	private byte quant_precision = 5;

	private byte bits_per_pixel = 8;

	private byte quant_type = 0;

	private boolean load_intra_quant_mat;

	private boolean load_nonintra_quant_mat;

	private int[] intra_quant_mat = DEFAULT_INTRA_QUANT_MAT;

	private int[] nonintra_quant_mat = DEFAULT_NON_INTRA_QUANT_MAT;

	private boolean load_intra_quant_mat_grayscale;

	private boolean load_nonintra_quant_mat_grayscale;

	private boolean quarter_sample = false;

	private boolean complexity_estimation_disable = true;

	private boolean resync_marker_disable = true;

	private boolean data_partitioned = false;

	private boolean reversible_vlc = false;

	private boolean newpred_enable = false;

	private byte requested_upstream_message_type;

	private boolean newpred_segment_type;

	private boolean reduced_resolution_vop_enable;

	private boolean scalability = false;

	private boolean hierarchy_type;

	private byte ref_layer_id;

	private boolean ref_layer_sampling_direc;

	private byte hor_sampling_factor_n;

	private byte hor_sampling_factor_m;

	private byte vert_sampling_factor_n;

	private byte vert_sampling_factor_m;

	private boolean enhancement_type;

	private boolean use_ref_shape;

	private boolean use_ref_texture;

	private byte shape_hor_sampling_factor_n;

	private byte shape_hor_sampling_factor_m;

	private byte shape_vert_sampling_factor_n;

	private byte shape_vert_sampling_factor_m;

	private byte estimation_method;

	private boolean shape_complexity_estimation_disable;

	private boolean opaque;

	private boolean transparent;

	private boolean intra_cae;

	private boolean inter_cae;

	private boolean no_update;

	private boolean upsampling;

	private boolean texture_complexity_estimation_set_1_disable;

	private boolean intra_blocks;

	private boolean inter_blocks;

	private boolean inter4v_blocks;

	private boolean not_coded_blocks;

	private boolean texture_complexity_estimation_set_2_disable;

	private boolean dct_coefs;

	private boolean dct_lines;

	private boolean vlc_symbols;

	private boolean vlc_bits;

	private boolean motion_compensation_complexity_disable;

	private boolean apm;

	private boolean npm;

	private boolean interpolate_mc_q;

	private boolean forw_back_mc_q;

	private boolean halfpel2;

	private boolean halfpel4;

	private boolean version2_complexity_estimation_disable;

	private boolean sadct;

	private boolean quarterpel;

	private byte sprite_transmit_mode;

	private void decode_VideoObjectLayer() throws IOException {
		// init predefined values
		bits_per_pixel = 8;
		quant_precision = 5;
		intra_quant_mat = DEFAULT_INTRA_QUANT_MAT;
		nonintra_quant_mat = DEFAULT_NON_INTRA_QUANT_MAT;
		quarter_sample = false;
		newpred_enable = false;
		sprite_transmit_mode = PIECE_TRANSMIT_MODE;
		transparent = false;
		data_partitioned = false;
		not_8_bit = false;

		short_video_header = false;
		random_accessible_vol = videoStream.next_bit();
		video_object_type_indication = (short) videoStream.next_bits(8);
		is_object_layer_identifier = videoStream.next_bit();
		if (is_object_layer_identifier) {
			video_object_layer_verid = (byte) videoStream.next_bits(4);
			video_object_layer_priority = (byte) videoStream.next_bits(3);
		}
		aspect_ratio_info = (byte) videoStream.next_bits(4);
		if (aspect_ratio_info == EXTENDED_ASPECT_RATIO) {
			par_width = (short) videoStream.next_bits(8);
			par_height = (short) videoStream.next_bits(8);
		}
		vol_control_parameters = videoStream.next_bit();
		if (vol_control_parameters) {
			chroma_format = (byte) videoStream.next_bits(2);
			low_delay = videoStream.next_bit();
			vbv_parameters = videoStream.next_bit();
			if (vbv_parameters) {
				first_half_bit_rate = (short) videoStream.next_bits(15);
				videoStream.marker_bit();
				latter_half_bit_rate = (short) videoStream.next_bits(15);
				videoStream.marker_bit();
				first_half_vbv_buffer_size = (short) videoStream.next_bits(15);
				videoStream.marker_bit();
				latter_half_vbv_buffer_size = (byte) videoStream.next_bits(3);
				first_half_vbv_occupancy = (short) videoStream.next_bits(11);
				videoStream.marker_bit();
				latter_half_vbv_occupancy = (short) videoStream.next_bits(15);
				videoStream.marker_bit();
			}
		}
		video_object_layer_shape = (byte) videoStream.next_bits(2);
		if ((video_object_layer_shape == GRAYSCALE_SHAPE)
				&& (video_object_layer_verid != 1)) {
			video_object_layer_shape_extension = (byte) videoStream
					.next_bits(4);
		}
		videoStream.marker_bit();
		vop_time_increment_resolution = (int) videoStream.next_bits(16);
		// calculates the vop_time_increment_length (number of bits required to
		// store vop_time_increment_resolution)
		vop_time_increment_length = 31;
		while (((vop_time_increment_resolution >>> vop_time_increment_length) == 0)
				&& (vop_time_increment_length > 0)) {
			vop_time_increment_length--;
		}
		vop_time_increment_length++;
		if (prev_vop_time_increment == -1) {
			prev_vop_time_increment = 0;
		}
		vop_id_length = vop_time_increment_length + 3;
		vop_id_length = vop_id_length > 15 ? 15 : vop_id_length;
		videoStream.marker_bit();
		fixed_vop_rate = videoStream.next_bit();
		if (fixed_vop_rate) {
			fixed_vop_time_increment = (int) videoStream
					.next_bits(vop_time_increment_length);
		}
		if (video_object_layer_shape != BINARY_ONLY_SHAPE) {
			if (video_object_layer_shape == RECTANGULAR_SHAPE) {
				videoStream.marker_bit();
				video_object_layer_width = (short) videoStream.next_bits(13);
				videoStream.marker_bit();
				video_object_layer_height = (short) videoStream.next_bits(13);
				videoStream.marker_bit();
			}
			interlaced = videoStream.next_bit();
			obmc_disable = videoStream.next_bit();
			if (video_object_layer_verid == 1) {
				sprite_enable = (byte) videoStream.next_bits(1);
			} else {
				sprite_enable = (byte) videoStream.next_bits(2);
			}
			if ((sprite_enable == STATIC_SPRITE)
					|| (sprite_enable == GMC_SPRITE)) {
				// skip sprite information
				if (sprite_enable != GMC_SPRITE) {
					videoStream.skip_bits(13 + 1 + 13 + 1 + 13 + 1 + 13 + 1);
				}
				no_of_sprite_warping_points = (byte) videoStream.next_bits(6);
				videoStream.skip_bits(2 + 1);
				if (sprite_enable != GMC_SPRITE) {
					low_latency_sprite_enable = videoStream.next_bit();
				}
			}
			if ((video_object_layer_verid != 1)
					&& (video_object_layer_shape != RECTANGULAR_SHAPE)) {
				sadct_disable = videoStream.next_bit();
			}
			not_8_bit = videoStream.next_bit();
			if (not_8_bit) {
				quant_precision = (byte) videoStream.next_bits(4);
				bits_per_pixel = (byte) videoStream.next_bits(4);
			}
			if (video_object_layer_shape == GRAYSCALE_SHAPE) {
				// skips the data of an 'grayscale' object
				videoStream.skip_bits(3);
			}
			quant_type = (byte) videoStream.next_bits(1);
			if (quant_type == 1) {
				load_intra_quant_mat = videoStream.next_bit();
				if (load_intra_quant_mat) {
					intra_quant_mat = read_quant_matrix();
				}
				load_nonintra_quant_mat = videoStream.next_bit();
				if (load_nonintra_quant_mat) {
					nonintra_quant_mat = read_quant_matrix();
				}
				if (video_object_layer_shape == GRAYSCALE_SHAPE) {
					// skip the matrix information for the grayscale object
					// layer
					for (int i = 0; i < aux_comp_count[video_object_layer_shape_extension]; i++) {
						load_intra_quant_mat_grayscale = videoStream.next_bit();
						if (load_intra_quant_mat_grayscale) {
							read_quant_matrix();
						}
						load_nonintra_quant_mat_grayscale = videoStream
								.next_bit();
						if (load_nonintra_quant_mat_grayscale) {
							read_quant_matrix();
						}
					}
				}
			}
			if (video_object_layer_verid != 1) {
				quarter_sample = videoStream.next_bit();
			}
			complexity_estimation_disable = videoStream.next_bit();
			if (!complexity_estimation_disable) {
				estimation_method = (byte) videoStream.next_bits(2);
				if ((estimation_method == 0) || (estimation_method == 1)) {
					shape_complexity_estimation_disable = videoStream
							.next_bit();
					if (!shape_complexity_estimation_disable) {
						opaque = videoStream.next_bit();
						transparent = videoStream.next_bit();
						intra_cae = videoStream.next_bit();
						inter_cae = videoStream.next_bit();
						no_update = videoStream.next_bit();
						upsampling = videoStream.next_bit();
					}
					texture_complexity_estimation_set_1_disable = videoStream
							.next_bit();
					if (!texture_complexity_estimation_set_1_disable) {
						intra_blocks = videoStream.next_bit();
						inter_blocks = videoStream.next_bit();
						inter4v_blocks = videoStream.next_bit();
						not_coded_blocks = videoStream.next_bit();
					}
					videoStream.marker_bit();
					texture_complexity_estimation_set_2_disable = videoStream
							.next_bit();
					if (!texture_complexity_estimation_set_2_disable) {
						dct_coefs = videoStream.next_bit();
						dct_lines = videoStream.next_bit();
						vlc_symbols = videoStream.next_bit();
						vlc_bits = videoStream.next_bit();
					}
					motion_compensation_complexity_disable = videoStream
							.next_bit();
					if (!motion_compensation_complexity_disable) {
						apm = videoStream.next_bit();
						npm = videoStream.next_bit();
						interpolate_mc_q = videoStream.next_bit();
						forw_back_mc_q = videoStream.next_bit();
						halfpel2 = videoStream.next_bit();
						halfpel4 = videoStream.next_bit();
					}
					videoStream.marker_bit();
					if (estimation_method == 1) {
						version2_complexity_estimation_disable = videoStream
								.next_bit();
						if (!version2_complexity_estimation_disable) {
							sadct = videoStream.next_bit();
							quarterpel = videoStream.next_bit();
						}
					}
				}
			}
			resync_marker_disable = videoStream.next_bit();
			data_partitioned = videoStream.next_bit();

			if (data_partitioned) {
				reversible_vlc = videoStream.next_bit();
			}
			if (video_object_layer_verid != 1) {
				newpred_enable = videoStream.next_bit();
				if (newpred_enable) {
					requested_upstream_message_type = (byte) videoStream
							.next_bits(2);
					newpred_segment_type = videoStream.next_bit();
				}
				reduced_resolution_vop_enable = videoStream.next_bit();
			}
			scalability = videoStream.next_bit();
			if (scalability) {
				hierarchy_type = videoStream.next_bit();
				ref_layer_id = (byte) videoStream.next_bits(4);
				ref_layer_sampling_direc = videoStream.next_bit();
				hor_sampling_factor_n = (byte) videoStream.next_bits(5);
				hor_sampling_factor_m = (byte) videoStream.next_bits(5);
				vert_sampling_factor_n = (byte) videoStream.next_bits(5);
				vert_sampling_factor_m = (byte) videoStream.next_bits(5);
				enhancement_type = videoStream.next_bit();
				if ((video_object_layer_shape == BINARY_SHAPE)
						&& (hierarchy_type == false)) {
					use_ref_shape = videoStream.next_bit();
					use_ref_texture = videoStream.next_bit();
					shape_hor_sampling_factor_n = (byte) videoStream
							.next_bits(5);
					shape_hor_sampling_factor_m = (byte) videoStream
							.next_bits(5);
					shape_vert_sampling_factor_n = (byte) videoStream
							.next_bits(5);
					shape_vert_sampling_factor_m = (byte) videoStream
							.next_bits(5);
				}
			}
		} else {
			if (video_object_layer_verid != 1) {
				scalability = videoStream.next_bit();
				if (scalability) {
					ref_layer_id = (byte) videoStream.next_bits(4);
					shape_hor_sampling_factor_n = (byte) videoStream
							.next_bits(5);
					shape_hor_sampling_factor_m = (byte) videoStream
							.next_bits(5);
					shape_vert_sampling_factor_n = (byte) videoStream
							.next_bits(5);
					shape_vert_sampling_factor_m = (byte) videoStream
							.next_bits(5);
				}
			}
			resync_marker_disable = videoStream.next_bit();
		}
	}

	/** The time (hours) of the group of video object planes. */
	private int time_code_hours = 0;

	/** The time (minutes) of the group of video object planes. */
	private int time_code_minutes = 0;

	/** The time (seconds) of the group of video object planes. */
	private int time_code_seconds = 0;

	/** The Closed Gov flag of the group of video object planes. */
	private boolean closed_gov = false;

	/** The Broken Link flag of the group of video object planes. */
	private boolean broken_link = false;

	private void decode_Group_of_VideoObjectPlane() throws IOException {
		vop_number_in_gop = 0;
		time_code_hours = (int) videoStream.next_bits(5);
		time_code_minutes = (int) videoStream.next_bits(6);
		videoStream.marker_bit();
		time_code_seconds = (int) videoStream.next_bits(6);
		closed_gov = videoStream.next_bit();
		broken_link = videoStream.next_bit();
		if (prev_vop_time_increment > 0) {
			prev_vop_time_increment = 0;
		}
		/*
		 * System.out.println("Group_of_VideoObjectPlane");
		 * System.out.println("Time Code Hours = " + time_code_hours);
		 * System.out.println("Time Code Minutes = " + time_code_minutes);
		 * System.out.println("Time Code Seconds = " + time_code_seconds);
		 * System.out.println("Closed Gov = " + closed_gov);
		 * System.out.println("Broken Link = " + broken_link);
		 */
		return;
	}

	/** The coding type of the VOP. */
	private int vop_coding_type;

	/** The number of seconds since the synchronization point. */
	private int modulo_time_base;

	private int vop_time_increment;

	private boolean vop_coded;

	/** The id of VOP. */
	private int vop_number_in_gop = 0;

	private short vop_id = 0;

	private boolean vop_id_for_prediction_indication;

	private short vop_id_for_prediction;

	private byte vop_rounding_type;

	private boolean vop_reduced_resolution;

	private byte intra_dc_vlc_thr;

	private boolean top_field_first;

	private boolean alternate_vertical_scan_flag;

	private short vop_width;

	private short vop_height;

	private short vop_horizontal_mc_spatial_ref;

	private short vop_vertical_mc_spatial_ref;

	private boolean background_composition;

	private boolean change_conv_ratio_disable;

	private boolean vop_constant_alpha;

	private short vop_constant_alpha_value;

	private short vop_quant;

	private byte vop_fcode_forward;

	private byte vop_fcode_backward;

	private boolean vop_shape_coding_type;

	private boolean load_backward_shape;

	private short backward_shape_width;

	private short backward_shape_height;

	private short backward_shape_horizontal_mc_spatial_ref;

	private short backward_shape_vertical_mc_spatial_ref;

	private boolean load_forward_shape;

	private short forward_shape_width;

	private short forward_shape_height;

	private short forward_shape_horizontal_mc_spatial_ref;

	private short forward_shape_vertical_mc_spatial_ref;

	private byte ref_select_code;

	private VideoFrame last_I_P_Frame = null;

	private VideoFrame currentFrame = null;

	private int prev_vop_time_increment = -1;

	private int base_time;

	private void decode_VideoObjectPlane() throws IOException {
		vop_rounding_type = 0;
		macroblock_number = 0;
		prevQp = -1;
		vop_width = video_object_layer_width;
		vop_height = video_object_layer_height;
		max_macroblock_number = ((vop_width + 15) / 16)
				* ((vop_height + 15) / 16) - 1;
		vop_coding_type = (int) videoStream.next_bits(2);
		if ((vop_coding_type != I_VOP) && (vop_coding_type != P_VOP)) {
			throw new IllegalArgumentException("Unsupported Frame (Type = "
					+ vop_coding_type + ")");
		}
		vop_id++;
		modulo_time_base = 0;
		while (videoStream.next_bits(1) == 1) {
			modulo_time_base++;
		}
		videoStream.marker_bit();
		vop_time_increment = (int) videoStream
				.next_bits(vop_time_increment_length);
		videoStream.marker_bit();
		if ((vop_time_increment < prev_vop_time_increment)
				&& ((prev_vop_time_increment - vop_time_increment) > (vop_time_increment_resolution / 2))) {
			time_code_seconds++;
			while (time_code_seconds >= 60) {
				time_code_seconds -= 60;
				time_code_minutes++;
			}
			while (time_code_minutes >= 60) {
				time_code_minutes -= 60;
				time_code_hours++;
			}
		}
		prev_vop_time_increment = vop_time_increment;

		// calculates the current playing time
		base_time = time_code_hours * 3600 + time_code_minutes * 60
				+ time_code_seconds;

		vop_coded = videoStream.next_bit();
		if (vop_coded) {
			// finds out the last I or P type frame for the forward prediction
			if ((currentFrame != null)
					&& ((currentFrame.getType() == I_VOP) || (currentFrame
							.getType() == P_VOP))) {
				VideoFrame unusedFrame = last_I_P_Frame;
				last_I_P_Frame = currentFrame;
				currentFrame = unusedFrame;
			} else {
				currentFrame = null;
			}
			// creates a VideoFrame object.
			if (currentFrame == null) {
				currentFrame = new VideoFrame(vop_coding_type,
						video_object_layer_width, video_object_layer_height,
						bits_per_pixel);
			} else {
				currentFrame.setType(vop_coding_type);
			}
			currentFrame
					.setPlaying_time(base_time
							* 1000
							+ ((1000 * vop_time_increment) / vop_time_increment_resolution));

			if (newpred_enable) {
				vop_id_for_prediction = vop_id = (short) videoStream
						.next_bits(vop_id_length);
				vop_id_for_prediction_indication = videoStream.next_bit();
				if (vop_id_for_prediction_indication) {
					vop_id_for_prediction = (short) videoStream
							.next_bits(vop_id_length);
				} else {
					vop_id_for_prediction--;
				}
				videoStream.marker_bit();
			}
			if ((video_object_layer_shape != BINARY_ONLY_SHAPE)
					&& ((vop_coding_type == P_VOP) || ((vop_coding_type == S_VOP) && (sprite_enable == GMC_SPRITE)))) {
				vop_rounding_type = (byte) videoStream.next_bits(1);
				currentFrame.setRounding_control(vop_rounding_type);
			}
			if ((reduced_resolution_vop_enable)
					&& (video_object_layer_shape == RECTANGULAR_SHAPE)
					&& ((vop_coding_type == P_VOP) || (vop_coding_type == I_VOP))) {
				vop_reduced_resolution = videoStream.next_bit();
			}
			if (video_object_layer_shape != RECTANGULAR_SHAPE) {
				if (!((sprite_enable == STATIC_SPRITE) && (vop_coding_type == I_VOP))) {
					vop_width = (short) videoStream.next_bits(13);
					videoStream.marker_bit();
					vop_height = (short) videoStream.next_bits(13);
					videoStream.marker_bit();
					vop_horizontal_mc_spatial_ref = (short) videoStream
							.next_bits(13);
					videoStream.marker_bit();
					vop_vertical_mc_spatial_ref = (short) videoStream
							.next_bits(13);
					videoStream.marker_bit();
				}
				if ((video_object_layer_shape != BINARY_ONLY_SHAPE)
						&& scalability && enhancement_type) {
					background_composition = videoStream.next_bit();
				}
				change_conv_ratio_disable = videoStream.next_bit();
				vop_constant_alpha = videoStream.next_bit();
				if (vop_constant_alpha) {
					vop_constant_alpha_value = (short) videoStream.next_bits(8);
				}
			}
			if (video_object_layer_shape != BINARY_ONLY_SHAPE) {
				if (!complexity_estimation_disable) {
					read_vop_complexity_estimation_header();
				}
			}
			if (video_object_layer_shape != BINARY_ONLY_SHAPE) {
				intra_dc_vlc_thr = (byte) videoStream.next_bits(3);
				// System.out.println("intra_dc_vlc_thr = " + intra_dc_vlc_thr);
				if (interlaced) {
					top_field_first = videoStream.next_bit();
					alternate_vertical_scan_flag = videoStream.next_bit();
				}
			}
			if (((sprite_enable == STATIC_SPRITE) || (sprite_enable == GMC_SPRITE))
					&& (vop_coding_type == S_VOP)) {
				// TODO add sprite functionality
				if (no_of_sprite_warping_points > 0) {

				}
				return;
			}
			if (video_object_layer_shape != BINARY_ONLY_SHAPE) {
				quantiser_scale = vop_quant = (short) videoStream
						.next_bits(quant_precision);
				if (video_object_layer_shape == GRAYSCALE_SHAPE) {
					// skip the data for the grayscale shape
					for (int i = 0; i < aux_comp_count[video_object_layer_shape_extension]; i++) {
						videoStream.skip_bits(6);
					}
				}
				if (vop_coding_type != I_VOP) {
					vop_fcode_forward = (byte) videoStream.next_bits(3);
				}
				if (vop_coding_type == B_VOP) {
					vop_fcode_backward = (byte) videoStream.next_bits(3);
				}
				if (reduced_resolution_vop_enable) {
					max_macroblock_number = ((video_object_layer_width + 15) / 16)
							* ((video_object_layer_height + 15) / 16);
				} else {
					max_macroblock_number = ((vop_width + 15) / 16)
							* ((vop_height + 15) / 16);
				}
				max_macroblock_number--;
				printVideoObjectPlane();

				if (!scalability) {
					if ((video_object_layer_shape != RECTANGULAR_SHAPE)
							&& (vop_coding_type != I_VOP)) {
						vop_shape_coding_type = videoStream.next_bit();
					}
					motion_shape_texture();
					while (videoStream.nextbits_byteAligned(17) == RESYNC_MARKER) {
						video_packet_header();
						motion_shape_texture();
					}
				} else {
					if (enhancement_type) {
						load_backward_shape = videoStream.next_bit();
						if (load_backward_shape) {
							backward_shape_width = (short) videoStream
									.next_bits(13);
							videoStream.marker_bit();
							backward_shape_height = (short) videoStream
									.next_bits(13);
							videoStream.marker_bit();
							backward_shape_horizontal_mc_spatial_ref = (short) videoStream
									.next_bits(13);
							videoStream.marker_bit();
							backward_shape_vertical_mc_spatial_ref = (short) videoStream
									.next_bits(13);
							videoStream.marker_bit();
							backward_shape();
							load_forward_shape = videoStream.next_bit();
							if (load_forward_shape) {
								forward_shape_width = (short) videoStream
										.next_bits(13);
								videoStream.marker_bit();
								forward_shape_height = (short) videoStream
										.next_bits(13);
								videoStream.marker_bit();
								forward_shape_horizontal_mc_spatial_ref = (short) videoStream
										.next_bits(13);
								videoStream.marker_bit();
								forward_shape_vertical_mc_spatial_ref = (short) videoStream
										.next_bits(13);
								videoStream.marker_bit();
								forward_shape();
							}
						}
					}
					ref_select_code = (byte) videoStream.next_bits(2);
					combined_motion_shape_texture();
				}

			} else {
				combined_motion_shape_texture();
				while (videoStream.nextbits_byteAligned(17) == RESYNC_MARKER) {
					video_packet_header();
					combined_motion_shape_texture();
				}
			}
			mpeg4.nextFrame(currentFrame);
		} else {
			printVideoObjectPlane();
			if (vop_coding_type == I_VOP) {
				// creates a VideoFrame object.
				// finds out the last I or P type frame for the forward
				// prediction
				if ((currentFrame != null)
						&& ((currentFrame.getType() == I_VOP) || (currentFrame
								.getType() == P_VOP))) {
					VideoFrame unusedFrame = last_I_P_Frame;
					last_I_P_Frame = currentFrame;
					currentFrame = unusedFrame;
				} else {
					currentFrame = null;
				}
				if (currentFrame == null) {
					currentFrame = new VideoFrame(vop_coding_type,
							video_object_layer_width,
							video_object_layer_height, bits_per_pixel);
				} else {
					currentFrame.setType(I_VOP);
				}
				currentFrame.clearFrame();
				mpeg4.nextFrame(currentFrame);
			}
		}
	}

	private void printVideoObjectPlane() {
		/*
		 * System.out.println("VideoObjectPlane()");
		 * System.out.println("Vop Id = " + vop_id);
		 * System.out.println("Vop Coding Type = " + vop_coding_type);
		 * System.out.println("Modulo Time Base = " + modulo_time_base);
		 * System.out.println("Vop Time Increment = " + vop_time_increment);
		 * System.out.println("Vop Coded = " + vop_coded);
		 * System.out.println("vop_rounding_type = " + vop_rounding_type); //
		 * System.out.println("max_macroblock_number = " +
		 * max_macroblock_number); System.out.println("Current Time:" +
		 * currentFrame.getPlaying_time());
		 * System.out.println("Time Code Hours = " + time_code_hours);
		 * System.out.println("Time Code Minutes = " + time_code_minutes);
		 * System.out.println("Time Code Seconds = " + time_code_seconds);
		 * 
		 * if(vop_coded) { if(newpred_enable) {
		 * System.out.println("vop_id_for_prediction_indication = " +
		 * vop_id_for_prediction_indication);
		 * System.out.println("vop_id_for_prediction = " +
		 * vop_id_for_prediction); } }
		 */
	}

	private short dcecs_opaque;

	private short dcecs_transparent;

	private short dcecs_intra_cae;

	private short dcecs_inter_cae;

	private short dcecs_no_update;

	private short dcecs_upsampling;

	private short dcecs_intra_blocks;

	private short dcecs_not_coded_blocks;

	private short dcecs_dct_coefs;

	private short dcecs_dct_lines;

	private short dcecs_vlc_symbols;

	private byte dcecs_vlc_bits;

	private short dcecs_sadct;

	private short dcecs_inter_blocks;

	private short dcecs_inter4v_blocks;

	private short dcecs_apm;

	private short dcecs_npm;

	private short dcecs_forw_back_mc_q;

	private short dcecs_halfpel2;

	private short dcecs_halfpel4;

	private short dcecs_quarterpel;

	private short dcecs_interpolate_mc_q;

	private void read_vop_complexity_estimation_header() throws IOException {
		if (estimation_method == 0) {
			if (vop_coding_type == I_VOP) {
				if (opaque)
					dcecs_opaque = (short) videoStream.next_bits(8);
				if (transparent)
					dcecs_transparent = (short) videoStream.next_bits(8);
				if (intra_cae)
					dcecs_intra_cae = (short) videoStream.next_bits(8);
				if (inter_cae)
					dcecs_inter_cae = (short) videoStream.next_bits(8);
				if (no_update)
					dcecs_no_update = (short) videoStream.next_bits(8);
				if (upsampling)
					dcecs_upsampling = (short) videoStream.next_bits(8);
				if (intra_blocks)
					dcecs_intra_blocks = (short) videoStream.next_bits(8);
				if (not_coded_blocks)
					dcecs_not_coded_blocks = (short) videoStream.next_bits(8);
				if (dct_coefs)
					dcecs_dct_coefs = (short) videoStream.next_bits(8);
				if (dct_lines)
					dcecs_dct_lines = (short) videoStream.next_bits(8);
				if (vlc_symbols)
					dcecs_vlc_symbols = (short) videoStream.next_bits(8);
				if (vlc_bits)
					dcecs_vlc_bits = (byte) videoStream.next_bits(4);
				if (sadct)
					dcecs_sadct = (short) videoStream.next_bits(8);
			}
			if (vop_coding_type == P_VOP) {
				if (opaque)
					dcecs_opaque = (short) videoStream.next_bits(8);
				if (transparent)
					dcecs_transparent = (short) videoStream.next_bits(8);
				if (intra_cae)
					dcecs_intra_cae = (short) videoStream.next_bits(8);
				if (inter_cae)
					dcecs_inter_cae = (short) videoStream.next_bits(8);
				if (no_update)
					dcecs_no_update = (short) videoStream.next_bits(8);
				if (upsampling)
					dcecs_upsampling = (short) videoStream.next_bits(8);
				if (intra_blocks)
					dcecs_intra_blocks = (short) videoStream.next_bits(8); // TODO
																			// 镳钼屦栩�
				if (not_coded_blocks)
					dcecs_not_coded_blocks = (short) videoStream.next_bits(8); // TODO
																				// 镳钼屦栩�
				if (dct_coefs)
					dcecs_dct_coefs = (short) videoStream.next_bits(8);
				if (dct_lines)
					dcecs_dct_lines = (short) videoStream.next_bits(8);
				if (vlc_symbols)
					dcecs_vlc_symbols = (short) videoStream.next_bits(8);
				if (vlc_bits)
					dcecs_vlc_bits = (byte) videoStream.next_bits(4);
				if (inter_blocks)
					dcecs_inter_blocks = (short) videoStream.next_bits(8);
				if (inter4v_blocks)
					dcecs_inter4v_blocks = (short) videoStream.next_bits(8);
				if (apm)
					dcecs_apm = (short) videoStream.next_bits(8);
				if (npm)
					dcecs_npm = (short) videoStream.next_bits(8);
				if (forw_back_mc_q)
					dcecs_forw_back_mc_q = (short) videoStream.next_bits(8);
				if (halfpel2)
					dcecs_halfpel2 = (short) videoStream.next_bits(8);
				if (halfpel4)
					dcecs_halfpel4 = (short) videoStream.next_bits(8);
				if (sadct)
					dcecs_sadct = (short) videoStream.next_bits(8);
				if (quarterpel)
					dcecs_quarterpel = (short) videoStream.next_bits(8);
			}
			if (vop_coding_type == B_VOP) {
				if (opaque)
					dcecs_opaque = (short) videoStream.next_bits(8);
				if (transparent)
					dcecs_transparent = (short) videoStream.next_bits(8);
				if (intra_cae)
					dcecs_intra_cae = (short) videoStream.next_bits(8);
				if (inter_cae)
					dcecs_inter_cae = (short) videoStream.next_bits(8);
				if (no_update)
					dcecs_no_update = (short) videoStream.next_bits(8);
				if (upsampling)
					dcecs_upsampling = (short) videoStream.next_bits(8);
				if (intra_blocks)
					dcecs_intra_blocks = (short) videoStream.next_bits(8);
				if (not_coded_blocks)
					dcecs_not_coded_blocks = (short) videoStream.next_bits(8);
				if (dct_coefs)
					dcecs_dct_coefs = (short) videoStream.next_bits(8);
				if (dct_lines)
					dcecs_dct_lines = (short) videoStream.next_bits(8);
				if (vlc_symbols)
					dcecs_vlc_symbols = (short) videoStream.next_bits(8);
				if (vlc_bits)
					dcecs_vlc_bits = (byte) videoStream.next_bits(4);
				if (inter_blocks)
					dcecs_inter_blocks = (short) videoStream.next_bits(8);
				if (inter4v_blocks)
					dcecs_inter4v_blocks = (short) videoStream.next_bits(8);
				if (apm)
					dcecs_apm = (short) videoStream.next_bits(8);
				if (npm)
					dcecs_npm = (short) videoStream.next_bits(8);
				if (forw_back_mc_q)
					dcecs_forw_back_mc_q = (short) videoStream.next_bits(8);
				if (halfpel2)
					dcecs_halfpel2 = (short) videoStream.next_bits(8);
				if (halfpel4)
					dcecs_halfpel4 = (short) videoStream.next_bits(8);
				if (interpolate_mc_q)
					dcecs_interpolate_mc_q = (short) videoStream.next_bits(8);
				if (sadct)
					dcecs_sadct = (short) videoStream.next_bits(8);
				if (quarterpel)
					dcecs_quarterpel = (short) videoStream.next_bits(8);
			}
			if ((vop_coding_type == S_VOP) && (sprite_enable == STATIC_SPRITE)) {
				if (intra_blocks)
					dcecs_intra_blocks = (short) videoStream.next_bits(8);
				if (not_coded_blocks)
					dcecs_not_coded_blocks = (short) videoStream.next_bits(8);
				if (dct_coefs)
					dcecs_dct_coefs = (short) videoStream.next_bits(8);
				if (dct_lines)
					dcecs_dct_lines = (short) videoStream.next_bits(8);
				if (vlc_symbols)
					dcecs_vlc_symbols = (short) videoStream.next_bits(8);
				if (vlc_bits)
					dcecs_vlc_bits = (byte) videoStream.next_bits(4);
				if (inter_blocks)
					dcecs_inter_blocks = (short) videoStream.next_bits(8);
				if (inter4v_blocks)
					dcecs_inter4v_blocks = (short) videoStream.next_bits(8);
				if (apm)
					dcecs_apm = (short) videoStream.next_bits(8);
				if (npm)
					dcecs_npm = (short) videoStream.next_bits(8);
				if (forw_back_mc_q)
					dcecs_forw_back_mc_q = (short) videoStream.next_bits(8);
				if (halfpel2)
					dcecs_halfpel2 = (short) videoStream.next_bits(8);
				if (halfpel4)
					dcecs_halfpel4 = (short) videoStream.next_bits(8);
				if (interpolate_mc_q)
					dcecs_interpolate_mc_q = (short) videoStream.next_bits(8);
			}
		}
		// System.out.println("read_vop_complexity_estimation_header()");
	}

	/**
	 * Loads the quantization matrix from the video stream.
	 * 
	 * @return the quantization matrix.
	 * @throws IOException
	 *             raises if an error occurs.
	 */
	private int[] read_quant_matrix() throws IOException {
		int i;
		int[] quant_matrix = new int[64];
		for (i = 0; i < 64; i++) {
			int quant_value = (int) videoStream.next_bits(8);
			quant_matrix[i] = quant_value;
			if ((i > 0) && (quant_value == 0)) {
				break;
			}
		}
		for (; i < 64; i++) {
			quant_matrix[i] = 0;
		}
		return quant_matrix;
	}

	private boolean header_extension_code;

	private short macroblock_number;

	private int macroblock_number_length;

	private int max_macroblock_number;

	private short quant_scale;

	private void video_packet_header() throws IOException {
		// System.out.println("video_packet_header()");
		videoStream.next_resyncmarker();
		if (video_object_layer_shape != RECTANGULAR_SHAPE) {
			header_extension_code = videoStream.next_bit();
			if (header_extension_code
					&& !((sprite_enable == STATIC_SPRITE) && (vop_coding_type == I_VOP))) {
				vop_width = (short) videoStream.next_bits(13);
				videoStream.marker_bit();
				vop_height = (short) videoStream.next_bits(13);
				videoStream.marker_bit();
				vop_horizontal_mc_spatial_ref = (short) videoStream
						.next_bits(13);
				videoStream.marker_bit();
				vop_vertical_mc_spatial_ref = (short) videoStream.next_bits(13);
				videoStream.marker_bit();
			}
		}
		macroblock_number_length = 13;
		while ((((max_macroblock_number + 1) >>> macroblock_number_length) == 0)
				&& (macroblock_number_length > 0)) {
			macroblock_number_length--;
		}
		macroblock_number_length++;
		macroblock_number = (short) videoStream
				.next_bits(macroblock_number_length);
		currentFrame.clearMacroblockInfo(macroblock_number);
		if (video_object_layer_shape != BINARY_ONLY_SHAPE) {
			quantiser_scale = quant_scale = (short) videoStream
					.next_bits(quant_precision);
		}
		if (video_object_layer_shape == RECTANGULAR_SHAPE) {
			header_extension_code = videoStream.next_bit();
		}
		if (header_extension_code) {
			modulo_time_base = 0;
			while (videoStream.next_bits(1) == 1) {
				modulo_time_base++;
			}
			videoStream.marker_bit();
			vop_time_increment = (int) videoStream
					.next_bits(vop_time_increment_length);
			videoStream.marker_bit();
			vop_coding_type = (byte) videoStream.next_bits(2);
			if (video_object_layer_shape != RECTANGULAR_SHAPE) {
				change_conv_ratio_disable = videoStream.next_bit();
				if (vop_coding_type != I_VOP) {
					vop_shape_coding_type = videoStream.next_bit();
				}
			}
			if (video_object_layer_shape != BINARY_ONLY_SHAPE) {
				intra_dc_vlc_thr = (byte) videoStream.next_bits(3);
				if ((sprite_enable == GMC_SPRITE) && (vop_coding_type == S_VOP)
						&& (no_of_sprite_warping_points > 0)) {
					sprite_trajectory();
				}
				if ((reduced_resolution_vop_enable)
						&& (video_object_layer_shape == RECTANGULAR_SHAPE)
						&& ((vop_coding_type == P_VOP) || (vop_coding_type == I_VOP))) {
					vop_reduced_resolution = videoStream.next_bit();
				}
				if (vop_coding_type != I_VOP) {
					vop_fcode_forward = (byte) videoStream.next_bits(3);
				}
				if (vop_coding_type == B_VOP) {
					vop_fcode_backward = (byte) videoStream.next_bits(3);
				}
			}
		}
		if (newpred_enable) {
			vop_id_for_prediction = vop_id = (short) videoStream
					.next_bits(vop_id_length);
			vop_id_for_prediction_indication = videoStream.next_bit();
			if (vop_id_for_prediction_indication) {
				vop_id_for_prediction = (short) videoStream
						.next_bits(vop_id_length);
			} else {
				vop_id_for_prediction--;
			}
			videoStream.marker_bit();
		}
	}

	private void motion_shape_texture() throws IOException {
		// System.out.println("motion_shape_texture()");
		if (data_partitioned) {
			data_partitioned_motion_shape_texture();
		} else {
			combined_motion_shape_texture();
		}
	}

	private void combined_motion_shape_texture() throws IOException {
		// System.out.println("combined_motion_shape_texture()");
		do {
			macroblock();
		} while ((macroblock_number <= max_macroblock_number)
				&& (videoStream.is_data_in_next_byte() || ((videoStream
						.nextbits_byteAligned(17) != RESYNC_MARKER) && (videoStream
						.nextbits_byteAligned(24) != 1))));
	}

	private void data_partitioned_motion_shape_texture() throws IOException {
		// TODO complete data_partitioned_motion_shape_texture() method
		// System.out.println("data_partitioned_motion_shape_texture()");
	}

	private boolean not_coded;

	private int mb_type;

	private int derived_mb_type;

	private int cbpc;

	private boolean mcsel;

	private boolean ac_pred_flag;

	private byte cbpy;

	private byte dquant;

	private int block_count = 6;

	private boolean use_intra_dc_vlc;

	private short Qp;

	private short prevQp;

	private short quantiser_scale;

	private void macroblock() throws IOException {
		// System.out.println("macroblock(" + macroblock_number + ")");

		// set default values
		if ((ref_select_code == 0) && scalability) {
			mb_type = 4 /* 'forward mc + Q' */;
		} else {
			mb_type = 1 /* 'direct' */;
		}

		not_coded = false;
		mcsel = false;
		ac_pred_flag = false;
		/*
		 * if((sprite_enable == GMC_SPRITE) && (vop_coding_type == S_VOP) &&
		 * not_coded) { mcsel = true; }
		 */
		cbpy = 15;
		cbpc = 3;

		/*
		 * Qp is defined as the DCT quantisation parameter for luminance and
		 * chrominance used for immediately previous coded macroblock, except
		 * for the first coded macroblock in a VOP or a video packet. At the
		 * first coded macroblock in a VOP or a video packet, the running Qp is
		 * defined as the quantisation parameter value for the current
		 * macroblock.
		 */
		if (vop_coding_type != B_VOP) {
			int[][] mcbpc_table = null;
			if ((vop_coding_type == I_VOP)
					|| ((vop_coding_type == S_VOP) && low_latency_sprite_enable && (sprite_transmit_mode == PIECE_TRANSMIT_MODE))) {
				mcbpc_table = Huffman.MCBPC_1_TAB;
			} else {
				mcbpc_table = Huffman.MCBPC_2_TAB;
			}
			if ((video_object_layer_shape != RECTANGULAR_SHAPE)
					&& !((sprite_enable == STATIC_SPRITE)
							&& low_latency_sprite_enable && (sprite_transmit_mode == UPDATE_TRANSMIT_MODE))) {
				mb_binary_shape_coding();
			}
			if (video_object_layer_shape != BINARY_ONLY_SHAPE) {
				if (!transparent_mb()) {
					if ((video_object_layer_shape != RECTANGULAR_SHAPE)
							&& !((sprite_enable == STATIC_SPRITE)
									&& low_latency_sprite_enable && (sprite_transmit_mode == UPDATE_TRANSMIT_MODE))) {
						do {
							if ((vop_coding_type != I_VOP)
									&& !((sprite_enable == STATIC_SPRITE) && (sprite_transmit_mode == PIECE_TRANSMIT_MODE))) {
								not_coded = videoStream.next_bit();
							}
							if (!not_coded
									|| (vop_coding_type == I_VOP)
									|| ((vop_coding_type == S_VOP)
											&& low_latency_sprite_enable && (sprite_transmit_mode == PIECE_TRANSMIT_MODE))) {
								// decode mcbpc
								int[] mcbpcValues = huffman.decode(9,
										mcbpc_table);
								derived_mb_type = mcbpcValues[2];
								cbpc = mcbpcValues[3];
							}

						} while (!(not_coded || (derived_mb_type != Huffman.MCBPC_STUFFING)));
					} else {
						if ((vop_coding_type != I_VOP)
								&& !((sprite_enable == STATIC_SPRITE) && (sprite_transmit_mode == PIECE_TRANSMIT_MODE))) {
							not_coded = videoStream.next_bit();
							if (not_coded && (vop_coding_type == P_VOP)) {
								currentFrame.copyMacroblock(last_I_P_Frame,
										macroblock_number);
							}
						}
						if (!not_coded
								|| (vop_coding_type == I_VOP)
								|| ((vop_coding_type == S_VOP)
										&& low_latency_sprite_enable && (sprite_transmit_mode == PIECE_TRANSMIT_MODE))) {
							// decode mcbpc
							int[] mcbpcValues = huffman.decode(9, mcbpc_table);
							derived_mb_type = mcbpcValues[2];
							cbpc = mcbpcValues[3];
						}
					}
					// set the default value for the mcsel
					if ((sprite_enable == GMC_SPRITE)
							&& (vop_coding_type == S_VOP) && not_coded) {
						mcsel = true;
					}
					if (!not_coded
							|| (vop_coding_type == I_VOP)
							|| ((vop_coding_type == S_VOP)
									&& low_latency_sprite_enable && (sprite_transmit_mode == PIECE_TRANSMIT_MODE))) {
						if ((vop_coding_type == S_VOP)
								&& (sprite_enable == GMC_SPRITE)
								&& ((derived_mb_type == 0) || (derived_mb_type == 1))) {
							mcsel = videoStream.next_bit();
						}
						if (!short_video_header
								&& ((derived_mb_type == 3) || (derived_mb_type == 4))) {
							ac_pred_flag = videoStream.next_bit();
						}
						if (derived_mb_type == Huffman.MCBPC_STUFFING) {
							// System.out.println("MCBPC_STUFFING");
							return;
						}
						// select the VLC table for cbpy in the case of one -
						// four non-transparent blocks
						int[][] cbpy_table = Huffman.CBPY_4_TAB;
						/*
						 * if(transparent_mb()) { switch(non_transparent_blocks)
						 * { case 1: cbpy_table = Huffman.CBPY_1_TAB; break;
						 * case 2: cbpy_table = Huffman.CBPY_2_TAB; break; case
						 * 3: cbpy_table = Huffman.CBPY_3_TAB; break; default:
						 * cbpy_table = Huffman.CBPY_4_TAB; break; } }
						 */
						// select the value for cbpy in the case of the intra or
						// the inter macroblock
						int cbpy_index = (derived_mb_type >= 3) ? 2 : 3;
						cbpy = (byte) huffman.decode(6, cbpy_table)[cbpy_index];
						if ((derived_mb_type == 1) || (derived_mb_type == 4)) {
							dquant = (byte) videoStream.next_bits(2);
							// apply dquant value
							switch (dquant) {
							case 0:
								quantiser_scale--;
								break;
							case 1:
								quantiser_scale -= 2;
								break;
							case 2:
								quantiser_scale++;
								break;
							case 3:
								quantiser_scale += 2;
								break;
							}
							if (quantiser_scale < 1) {
								quantiser_scale = 1;
							}
							if (quantiser_scale > ((1 << quant_precision) - 1)) {
								quantiser_scale = (short) ((1 << quant_precision) - 1);
							}
						}
						if (interlaced) {
							interlaced_information();
						}
						if (!((ref_select_code == 3) && scalability)
								&& (sprite_enable != STATIC_SPRITE)) {
							if (((derived_mb_type == 0) || (derived_mb_type == 1))
									&& ((vop_coding_type == P_VOP) || ((vop_coding_type == S_VOP) && !mcsel))) {
								motion_vector(FORWARD_MOTION_MODE);
								currentFrame.setForwardMotionVector(
										macroblock_number, -1, quarter_sample,
										vop_fcode_forward, horizontal_mv_data,
										horizontal_mv_residual,
										vertical_mv_data, vertical_mv_residual);
								if (interlaced && field_prediction) {
									motion_vector(FORWARD_MOTION_MODE);
								}
							}
							if (derived_mb_type == 2) {
								for (int j = 0; j < 4; j++) {
									if (!transparent_block(j)) {
										motion_vector(FORWARD_MOTION_MODE);
										currentFrame.setForwardMotionVector(
												macroblock_number, j,
												quarter_sample,
												vop_fcode_forward,
												horizontal_mv_data,
												horizontal_mv_residual,
												vertical_mv_data,
												vertical_mv_residual);
									}
								}
							}
						}
						set_use_intra_dc_vlc();

						currentFrame.setMacroblockInfo(macroblock_number,
								derived_mb_type, quantiser_scale);
						/*
						 * if((vop_id == 9) && ((macroblock_number == 21) ||
						 * (macroblock_number == 20) || (macroblock_number ==
						 * 11) || (macroblock_number == 10))) {
						 * System.out.println("macroblock_number = " +
						 * macroblock_number);
						 * System.out.println("derived_mb_type = " +
						 * derived_mb_type); if ((derived_mb_type == 1) ||
						 * (derived_mb_type == 4)) {
						 * System.out.println("dquant = " + dquant); }
						 * System.out.println("not_coded = " + not_coded);
						 * System.out.println("cbpy = " + cbpy);
						 * System.out.println("cbpc = " + cbpc);
						 * System.out.println("quantiser_scale = " +
						 * quantiser_scale); if((derived_mb_type == 3) ||
						 * (derived_mb_type == 4)) {
						 * System.out.println("ac_pred_flag = " + ac_pred_flag);
						 * System.out.println("use_intra_dc_vlc = " +
						 * use_intra_dc_vlc); } }
						 */
						for (int i = 0; i < block_count; i++) {
							if (!transparent_block(i)) {
								block(i);
							}
						}

						if (!((ref_select_code == 3) && scalability)
								&& (sprite_enable != STATIC_SPRITE)) {
							if (((derived_mb_type == 0)
									|| (derived_mb_type == 1) || (derived_mb_type == 2))
									&& ((vop_coding_type == P_VOP) || ((vop_coding_type == S_VOP) && !mcsel))) {
								currentFrame.applyForwardMotionVector(
										last_I_P_Frame, macroblock_number);
							}
						}

					}
				}
			}
		} else {
			// TODO complete macroblock()
			// System.out.println("Unsupported macroblock()...");
		}
		macroblock_number++;
	}

	/**
	 * Sets the use_intra_dc_vlc flag for the intra coded macroblocks.
	 */
	private void set_use_intra_dc_vlc() {
		// finds running Qp value
		if (prevQp == -1) {
			prevQp = Qp = quantiser_scale;
		} else {
			Qp = prevQp;
			prevQp = quantiser_scale;
		}
		if ((derived_mb_type == 3) || (derived_mb_type == 4)) {
			switch (intra_dc_vlc_thr) {
			case 0:
				use_intra_dc_vlc = true;
				break;
			case 1:
				use_intra_dc_vlc = Qp >= 13 ? false : true;
				break;
			case 2:
				use_intra_dc_vlc = Qp >= 15 ? false : true;
				break;
			case 3:
				use_intra_dc_vlc = Qp >= 17 ? false : true;
				break;
			case 4:
				use_intra_dc_vlc = Qp >= 19 ? false : true;
				break;
			case 5:
				use_intra_dc_vlc = Qp >= 21 ? false : true;
				break;
			case 6:
				use_intra_dc_vlc = Qp >= 23 ? false : true;
				break;
			case 7:
				use_intra_dc_vlc = false;
				break;
			}
		}
	}

	/**
	 * The 1-bit flag indicating whether the macroblock is frame (false) DCT
	 * coded or field (true) DCT coded.
	 */
	private boolean dct_type;

	private boolean field_prediction;

	private boolean forward_top_field_reference;

	private boolean forward_bottom_field_reference;

	private boolean backward_top_field_reference;

	private boolean backward_bottom_field_reference;

	private void interlaced_information() throws IOException {
		dct_type = false;

		if ((derived_mb_type == 3) || (derived_mb_type == 4)
				|| ((cbpy << 2 + cbpc) != 0)) { // TODO check this code (cbp
												// 玎戾礤眍 磬 (cbpy << 2 + cbpc))
			dct_type = videoStream.next_bit();
		}
		if (((vop_coding_type == P_VOP) && ((derived_mb_type == 0) || (derived_mb_type == 1)))
				|| ((sprite_enable == GMC_SPRITE) && (vop_coding_type == S_VOP)
						&& (derived_mb_type < 2) && (!mcsel))
				|| ((vop_coding_type == B_VOP) && (mb_type != 1 /* '1' */))) {
			field_prediction = videoStream.next_bit();
			if (field_prediction) {
				if ((vop_coding_type == P_VOP)
						|| ((vop_coding_type == B_VOP) && (mb_type != 3 /* '001' */))) {
					forward_top_field_reference = videoStream.next_bit();
					forward_bottom_field_reference = videoStream.next_bit();
				}
				if ((vop_coding_type == B_VOP) && (mb_type != 4/* '0001' */)) {
					backward_top_field_reference = videoStream.next_bit();
					backward_bottom_field_reference = videoStream.next_bit();
				}
			}
		}
		// System.out.println("interlaced_information()");
		// System.out.println("dct_type = " + dct_type);

	}

	private boolean transparent_mb() {
		// TODO complete transparent_mb()
		/*
		 * if(!transparent) return false;
		 */
		return false;
	}

	/**
	 * Returns <tt>true</tt> if the 8x8 block with index <code>n</code> consists
	 * only transparent pixels.
	 * 
	 * @param n
	 *            the number of the block to test.
	 * @return <tt>true</tt> if the 8x8 block with index <code>n</code> consists
	 *         only transparent pixels.
	 */
	private boolean transparent_block(int n) {
		// TODO complete transparent_block()
		return false;
	}

	private void mb_binary_shape_coding() throws IOException {
		// TODO complete mb_binary_shape_coding()
		// System.out.println("mb_binary_shape_coding()");
	}

	private void backward_shape() throws IOException {
		// TODO complete backward_shape()
		// System.out.println("backward_shape()");
	}

	private void forward_shape() throws IOException {
		// TODO complete forward_shape()
		// System.out.println("forward_shape()");
	}

	private void sprite_trajectory() throws IOException {
		// TODO complete sprite_trajectory()
		// System.out.println("sprite_trajectory()");
	}

	private int horizontal_mv_data;

	private byte horizontal_mv_residual;

	private int vertical_mv_data;

	private byte vertical_mv_residual;

	private void motion_vector(int mode) throws IOException {
		// System.out.println("motion_vector(" + mode + ")");
		// videoStream.print_next_bits(13);

		horizontal_mv_residual = 0;
		vertical_mv_residual = 0;
		if (mode == DIRECT_MOTION_MODE) {
			horizontal_mv_data = huffman.decode(13, Huffman.MVD_TAB)[2];
			vertical_mv_data = huffman.decode(13, Huffman.MVD_TAB)[2];
		} else if (mode == FORWARD_MOTION_MODE) {
			horizontal_mv_data = huffman.decode(13, Huffman.MVD_TAB)[2];
			if ((vop_fcode_forward != 1) && (horizontal_mv_data != 0)) {
				horizontal_mv_residual = (byte) videoStream
						.next_bits(vop_fcode_forward - 1);
			}
			vertical_mv_data = huffman.decode(13, Huffman.MVD_TAB)[2];
			if ((vop_fcode_forward != 1) && (vertical_mv_data != 0)) {
				vertical_mv_residual = (byte) videoStream
						.next_bits(vop_fcode_forward - 1);
			}
		} else if (mode == BACKWARD_MOTION_MODE) {
			horizontal_mv_data = huffman.decode(13, Huffman.MVD_TAB)[2];
			if ((vop_fcode_backward != 1) && (horizontal_mv_data != 0))
				horizontal_mv_residual = (byte) videoStream
						.next_bits(vop_fcode_backward - 1);
			vertical_mv_data = huffman.decode(13, Huffman.MVD_TAB)[2];
			if ((vop_fcode_backward != 1) && (vertical_mv_data != 0))
				vertical_mv_residual = (byte) videoStream
						.next_bits(vop_fcode_backward - 1);
		}
		/*
		 * System.out.println("vop_fcode_forward = " + vop_fcode_forward);
		 * System.out.println("horizontal_mv_data = " + horizontal_mv_data);
		 * System.out.println("horizontal_mv_residual = " +
		 * horizontal_mv_residual); System.out.println("vertical_mv_data = " +
		 * vertical_mv_data); System.out.println("vertical_mv_residual = " +
		 * vertical_mv_residual);
		 */
	}

	private int intra_dc_coefficient;

	private int dct_dc_size_luminance;

	private int dct_dc_size_chrominance;

	private int dct_dc_differential;

	private int dc_scaler;

	private int[] dct_coeff = new int[100];

	private boolean vertical_prediction;

	private boolean macroblock_intra;

	/**
	 * Reads the 8x8 block with the index <code>n</code> from the video stream.
	 * 
	 * @param n
	 *            the index of the block.
	 * @throws IOException
	 *             raises if an error occurs.
	 */
	private void block(int n) throws IOException {
		// System.out.println("block(" + n + ")");
		int coeff_pointer = 0;
		int[][] t_coeff = currentFrame.getBlock(macroblock_number, n);
		// resets the coeff block with zero values
		for (int i = 0; i < 64; i += 4) {
			dct_coeff[i] = dct_coeff[i + 1] = dct_coeff[i + 2] = dct_coeff[i + 3] = 0;
		}
		for (int i = 0; i < 8; i++) {
			t_coeff[i][0] = t_coeff[i][1] = t_coeff[i][2] = t_coeff[i][3] = 0;
			t_coeff[i][4] = t_coeff[i][5] = t_coeff[i][6] = t_coeff[i][7] = 0;
		}
		macroblock_intra = (derived_mb_type == 3) || (derived_mb_type == 4);
		// calculate dc_scaler for intra macroblock
		if (macroblock_intra) {
			if (short_video_header == true) {
				dc_scaler = 8;
			} else {
				if (n < 4) {
					if (quantiser_scale <= 4) {
						dc_scaler = 8;
					} else if (quantiser_scale <= 8) {
						dc_scaler = quantiser_scale * 2;
					} else if (quantiser_scale <= 24) {
						dc_scaler = quantiser_scale + 8;
					} else {
						dc_scaler = quantiser_scale * 2 - 16;
					}
				} else {
					if (quantiser_scale <= 4) {
						dc_scaler = 8;
					} else if (quantiser_scale <= 24) {
						dc_scaler = (quantiser_scale + 13) / 2;
					} else {
						dc_scaler = quantiser_scale - 6;
					}
				}
			}
		}
		boolean last = false;
		if (!data_partitioned && macroblock_intra) {
			if (short_video_header == true) {
				intra_dc_coefficient = (int) videoStream.next_bits(8);
				dct_coeff[0] = intra_dc_coefficient;
				coeff_pointer++;
			} else if (use_intra_dc_vlc) {
				if (n < 4) {
					dct_dc_size_luminance = huffman.decode(11,
							Huffman.DCT_DC_SIZE_LUMINANCE_TAB)[2];
					if (dct_dc_size_luminance != 0) {
						dct_dc_differential = (int) videoStream
								.next_bits(dct_dc_size_luminance);
						if ((dct_dc_differential & (1 << (dct_dc_size_luminance - 1))) == 0) {
							dct_dc_differential = (dct_dc_differential | (Integer.MIN_VALUE >> (31 - dct_dc_size_luminance))) + 1;
						}
					} else {
						dct_dc_differential = 0;
					}
					if (dct_dc_size_luminance > 8) {
						videoStream.marker_bit();
					}
				} else {
					dct_dc_size_chrominance = huffman.decode(12,
							Huffman.DCT_DC_SIZE_CHROMINANCE_TAB)[2];
					if (dct_dc_size_chrominance != 0) {
						dct_dc_differential = (int) videoStream
								.next_bits(dct_dc_size_chrominance);
						if ((dct_dc_differential & (1 << (dct_dc_size_chrominance - 1))) == 0) {
							dct_dc_differential = (dct_dc_differential | (Integer.MIN_VALUE >> (31 - dct_dc_size_chrominance))) + 1;
						}
					} else {
						dct_dc_differential = 0;
					}
					if (dct_dc_size_chrominance > 8) {
						videoStream.marker_bit();
					}
				}
				dct_coeff[0] = dct_dc_differential;
				coeff_pointer++;
			}
		}
		int[][] tcoeff_tab = null;
		int[][][] lmax_tab = null;
		int[][][] rmax_tab = null;
		if ((short_video_header == false) && macroblock_intra) {
			tcoeff_tab = Huffman.INTRA_TCOEF_TAB;
			lmax_tab = INTRA_LMAX_TAB;
			rmax_tab = INTRA_RMAX_TAB;
		} else {
			tcoeff_tab = Huffman.INTER_TCOEF_TAB;
			lmax_tab = INTER_LMAX_TAB;
			rmax_tab = INTER_RMAX_TAB;
		}
		if (pattern_code(n)) {
			while (!last) {
				int run = 0;
				int level = 0;
				int type = 0;
				try {
					// read DCT coefficient from the stream
					int[] values = huffman.decode(12, tcoeff_tab);
					if (values[2] != Huffman.TCOEF_ESCAPE) {
						last = values[2] == 1;
						run = values[3];
						// test the sign of the level
						if (videoStream.next_bit()) {
							level = -values[4];
						} else {
							level = values[4];
						}
						for (int i = 0; i < run; i++) {
							dct_coeff[coeff_pointer++] = 0;
						}
						dct_coeff[coeff_pointer++] = level;
					} else {
						// escape sequence
						if (short_video_header == true) {
							// read the ESCAPE sequence
							type = 1;
							last = videoStream.next_bit();
							run = (int) videoStream.next_bits(6);
							level = 0;
							if (videoStream.next_bit()) {
								level = (int) videoStream.next_bits(7) - 128;
							} else {
								level = (int) videoStream.next_bits(7);
							}
							for (int i = 0; i < run; i++) {
								dct_coeff[coeff_pointer++] = 0;
							}
							dct_coeff[coeff_pointer++] = level;
						} else if (!videoStream.next_bit()) {
							// type 1 of the escape sequence
							type = 2;
							values = huffman.decode(12, tcoeff_tab);
							last = values[2] == 1;
							int lastValue = values[2];
							run = values[3];
							level = values[4];
							// finds lmax value for the combination of run and
							// last
							for (int i = 0; i < lmax_tab[lastValue].length; i++) {
								if (run <= lmax_tab[lastValue][i][0]) {
									level = level + lmax_tab[lastValue][i][1];
									break;
								}
							}
							// test the sign of the level
							if (videoStream.next_bit()) {
								level = -level;
							}
							for (int i = 0; i < run; i++) {
								dct_coeff[coeff_pointer++] = 0;
							}
							dct_coeff[coeff_pointer++] = level;
						} else if (!videoStream.next_bit()) {
							type = 3;
							values = huffman.decode(12, tcoeff_tab);
							last = values[2] == 1;
							int lastValue = values[2];
							run = values[3];
							level = values[4];
							// finds rmax value as a function of the decoded
							// values of level and last
							run++;
							for (int i = 0; i < rmax_tab[lastValue].length; i++) {
								if (level <= rmax_tab[lastValue][i][0]) {
									run = run + rmax_tab[lastValue][i][1];
									break;
								}
							}
							// test the sign of the level
							if (videoStream.next_bit()) {
								level = -level;
							}
							for (int i = 0; i < run; i++) {
								dct_coeff[coeff_pointer++] = 0;
							}
							dct_coeff[coeff_pointer++] = level;
						} else {
							type = 4;
							last = videoStream.next_bit();
							run = (int) videoStream.next_bits(6);
							videoStream.marker_bit();
							if (videoStream.next_bit()) {
								level = (int) videoStream.next_bits(11) - 2048;
							} else {
								level = (int) videoStream.next_bits(11);
							}
							videoStream.marker_bit();
							for (int i = 0; i < run; i++) {
								dct_coeff[coeff_pointer++] = 0;
							}
							dct_coeff[coeff_pointer++] = level;
						}
					}
				} catch (RuntimeException ex) {
					System.out.println("vop_id = " + vop_id);
					System.out.println("macroblock_number = "
							+ macroblock_number);
					System.out.println("n = " + n);
					System.out.println("type = " + type);
					System.out.println("last = " + last);
					System.out.println("run = " + run);
					System.out.println("level = " + level);
					throw ex;
				}
			}
		}

		int[] scan_table = ZIGZAG_SCAN_TABLE;

		boolean use_intra_prediction = (short_video_header == false)
				&& macroblock_intra;
		if (use_intra_prediction) {
			// find prediction direction
			vertical_prediction = currentFrame.getPredictionDirection(
					macroblock_number, n);

			if (ac_pred_flag) {
				// select alternate scan table
				if (vertical_prediction) {
					scan_table = ALTERNATE_HORIZONTAL_SCAN_TABLE;
				} else {
					scan_table = ALTERNATE_VERTICAL_SCAN_TABLE;
				}
			}
		}

		// re-order of coefficients into a two-dimension array
		for (int i = 0; i < coeff_pointer; i++) {
			int index = scan_table[i];
			t_coeff[index >> 3][index & 7] = dct_coeff[i];
		}
		if (use_intra_prediction) {
			int[][] previous_block = currentFrame.getPreviousBlock();
			// DC coefficient prediction
			t_coeff[0][0] = saturate_coefficient(t_coeff[0][0]
					+ integer_round_div(previous_block[0][0], dc_scaler));
			if (ac_pred_flag) {
				// AC coefficients prediction
				int previous_quantiser_scale = currentFrame
						.getPreviousQuantiserScale();
				if (previous_quantiser_scale > 0) {
					if (vertical_prediction) {
						for (int i = 1; i < 8; i++) {
							t_coeff[0][i] = saturate_coefficient(t_coeff[0][i]
									+ integer_round_div(previous_block[8][i]
											* previous_quantiser_scale,
											quantiser_scale));
						}
					} else {
						for (int i = 1; i < 8; i++) {
							t_coeff[i][0] = saturate_coefficient(t_coeff[i][0]
									+ integer_round_div(previous_block[i][8]
											* previous_quantiser_scale,
											quantiser_scale));
						}
					}
					// fixs the pointer of last coeff in the block
					if (coeff_pointer < 14) {
						coeff_pointer = 14;
					}
				}
			}
		}
		if (macroblock_intra) {
			for (int i = 1; i < 8; i++) {
				t_coeff[i][8] = t_coeff[i][0];
				t_coeff[8][i] = t_coeff[0][i];
			}
		}
		/*
		 * for(int i = 0; i < 8; i++) { for(int j = 0; j < 8; j++) {
		 * t_coeff[i][j] = saturate_coefficient(t_coeff[i][j]); } }
		 */
		// inverse quantization
		int max_value = 1 << (bits_per_pixel + 3);
		if (quant_type == 1) {
			// first quantization method
			int sum = 0;
			int index, i, j;
			for (int coeff_number = 0; coeff_number < coeff_pointer; coeff_number++) {
				index = scan_table[coeff_number];
				i = index >> 3;
				j = index & 7;
				if (t_coeff[i][j] != 0) {
					if ((i == 0) && (j == 0) && macroblock_intra) {
						t_coeff[0][0] = t_coeff[0][0] * dc_scaler;
					} else {
						if (macroblock_intra) {
							t_coeff[i][j] = (t_coeff[i][j]
									* intra_quant_mat[i << 3 + j]
									* quantiser_scale * 2) / 16;
						} else {
							t_coeff[i][j] = ((t_coeff[i][j] * 2 + (t_coeff[i][j] >= 0 ? 1
									: -1))
									* nonintra_quant_mat[i << 3 + j] * quantiser_scale) / 16;
						}
					}
					// saturate coefficient
					if (t_coeff[i][j] > max_value) {
						t_coeff[i][j] = max_value;
					} else if (t_coeff[i][j] < -max_value) {
						t_coeff[i][j] = -max_value;
					}
					sum = sum + t_coeff[i][j];
				}
			}
			if ((sum & 1) == 0) {
				if ((t_coeff[7][7] & 1) != 0) {
					t_coeff[7][7]--;
				} else {
					t_coeff[7][7]++;
				}
			}
		} else {
			// second quantization method
			int event_addition = (quantiser_scale & 1) ^ 1;
			int index, i, j;
			for (int coeff_number = 0; coeff_number < coeff_pointer; coeff_number++) {
				index = scan_table[coeff_number];
				i = index >> 3;
				j = index & 7;
				if (t_coeff[i][j] != 0) {
					if ((i == 0) && (j == 0) && macroblock_intra) {
						t_coeff[0][0] = t_coeff[0][0] * dc_scaler;
					} else {
						t_coeff[i][j] = ((2 * Math.abs(t_coeff[i][j]) + 1)
								* quantiser_scale - event_addition)
								* (t_coeff[i][j] >= 0 ? 1 : -1);
					}
					// saturate coefficient
					if (t_coeff[i][j] >= max_value) {
						t_coeff[i][j] = max_value - 1;
					} else if (t_coeff[i][j] < -max_value) {
						t_coeff[i][j] = -max_value;
					}
				}
			}
		}

		currentFrame.transformBlock(macroblock_number, n);
	}

	/**
	 * Returns <tt>true</tt> if the 8x8 block with index <code>n</code> is coded
	 * (present in the bitstream).
	 * 
	 * @param n
	 *            the number of the block to test.
	 * @return <tt>true</tt> if the 8x8 block with index <code>n</code> is coded
	 *         (present in the bitstream).
	 */
	private boolean pattern_code(int n) {
		if (n < 4) {
			// luminance block
			return (cbpy & (1 << (3 - n))) > 0;
		}
		// chrominance block
		return (cbpc & (1 << (5 - n))) > 0;
	}

	private int integer_round_div(int a, int b) {
		int tmp = (a << 1) / b;
		return tmp >= 0 ? (tmp + 1) >> 1 : -((-tmp + 1) >> 1);
	}

	private int saturate_coefficient(int value) {
		if (value > 2047) {
			value = 2047;
		} else if (value < -2048) {
			value = -2048;
		}
		return value;
	}

	public static int[] getALTERNATE_HORIZONTAL_SCAN_TABLE() {
		return ALTERNATE_HORIZONTAL_SCAN_TABLE;
	}

	public static void setALTERNATE_HORIZONTAL_SCAN_TABLE(
			int[] alternate_horizontal_scan_table) {
		ALTERNATE_HORIZONTAL_SCAN_TABLE = alternate_horizontal_scan_table;
	}

	public static int[] getALTERNATE_VERTICAL_SCAN_TABLE() {
		return ALTERNATE_VERTICAL_SCAN_TABLE;
	}

	public static void setALTERNATE_VERTICAL_SCAN_TABLE(
			int[] alternate_vertical_scan_table) {
		ALTERNATE_VERTICAL_SCAN_TABLE = alternate_vertical_scan_table;
	}

	public static int[] getAux_comp_count() {
		return aux_comp_count;
	}

	public static int getB_VOP() {
		return B_VOP;
	}

	public static byte getBACKWARD_MOTION_MODE() {
		return BACKWARD_MOTION_MODE;
	}

	public static byte getBINARY_ONLY_SHAPE() {
		return BINARY_ONLY_SHAPE;
	}

	public static byte getBINARY_SHAPE() {
		return BINARY_SHAPE;
	}

	public static int[] getDEFAULT_INTRA_QUANT_MAT() {
		return DEFAULT_INTRA_QUANT_MAT;
	}

	public static int[] getDEFAULT_NON_INTRA_QUANT_MAT() {
		return DEFAULT_NON_INTRA_QUANT_MAT;
	}

	public static byte getDIRECT_MOTION_MODE() {
		return DIRECT_MOTION_MODE;
	}

	public static int getEXTENDED_ASPECT_RATIO() {
		return EXTENDED_ASPECT_RATIO;
	}

	public static byte getFORWARD_MOTION_MODE() {
		return FORWARD_MOTION_MODE;
	}

	public static byte getGMC_SPRITE() {
		return GMC_SPRITE;
	}

	public static byte getGRAYSCALE_SHAPE() {
		return GRAYSCALE_SHAPE;
	}

	public static int getGROUP_VOP_START_CODE() {
		return GROUP_VOP_START_CODE;
	}

	public static int getI_VOP() {
		return I_VOP;
	}

	public static int[][][] getINTER_LMAX_TAB() {
		return INTER_LMAX_TAB;
	}

	public static int[][][] getINTER_RMAX_TAB() {
		return INTER_RMAX_TAB;
	}

	public static byte getINTERPOLATE_MOTION_MODE() {
		return INTERPOLATE_MOTION_MODE;
	}

	public static int[][][] getINTRA_LMAX_TAB() {
		return INTRA_LMAX_TAB;
	}

	public static int[][][] getINTRA_RMAX_TAB() {
		return INTRA_RMAX_TAB;
	}

	public static byte getNOT_USED_SPRITE() {
		return NOT_USED_SPRITE;
	}

	public static int getP_VOP() {
		return P_VOP;
	}

	public static byte getPAUSE_TRANSMIT_MODE() {
		return PAUSE_TRANSMIT_MODE;
	}

	public static byte getPIECE_TRANSMIT_MODE() {
		return PIECE_TRANSMIT_MODE;
	}

	public static byte getRECTANGULAR_SHAPE() {
		return RECTANGULAR_SHAPE;
	}

	public static int getRESYNC_MARKER() {
		return RESYNC_MARKER;
	}

	public static int getS_VOP() {
		return S_VOP;
	}

	public static byte getSTATIC_SPRITE() {
		return STATIC_SPRITE;
	}

	public static byte getSTOP_TRANSMIT_MODE() {
		return STOP_TRANSMIT_MODE;
	}

	public static byte getUPDATE_TRANSMIT_MODE() {
		return UPDATE_TRANSMIT_MODE;
	}

	public static int getVIDEO_OBJECT_LAYER_START_CODE() {
		return VIDEO_OBJECT_LAYER_START_CODE;
	}

	public static int getVIDEO_OBJECT_START_CODE() {
		return VIDEO_OBJECT_START_CODE;
	}

	public static int getVISUAL_OBJECT_SEQUENCE_START_CODE() {
		return VISUAL_OBJECT_SEQUENCE_START_CODE;
	}

	public static int getVISUAL_OBJECT_START_CODE() {
		return VISUAL_OBJECT_START_CODE;
	}

	public static int getVOP_START_CODE() {
		return VOP_START_CODE;
	}

	public static int[] getZIGZAG_SCAN_TABLE() {
		return ZIGZAG_SCAN_TABLE;
	}

	public static void setZIGZAG_SCAN_TABLE(int[] zigzag_scan_table) {
		ZIGZAG_SCAN_TABLE = zigzag_scan_table;
	}

	public boolean isAc_pred_flag() {
		return ac_pred_flag;
	}

	public void setAc_pred_flag(boolean ac_pred_flag) {
		this.ac_pred_flag = ac_pred_flag;
	}

	public boolean isAlternate_vertical_scan_flag() {
		return alternate_vertical_scan_flag;
	}

	public void setAlternate_vertical_scan_flag(
			boolean alternate_vertical_scan_flag) {
		this.alternate_vertical_scan_flag = alternate_vertical_scan_flag;
	}

	public boolean isApm() {
		return apm;
	}

	public void setApm(boolean apm) {
		this.apm = apm;
	}

	public byte getAspect_ratio_info() {
		return aspect_ratio_info;
	}

	public void setAspect_ratio_info(byte aspect_ratio_info) {
		this.aspect_ratio_info = aspect_ratio_info;
	}

	public boolean isBackground_composition() {
		return background_composition;
	}

	public void setBackground_composition(boolean background_composition) {
		this.background_composition = background_composition;
	}

	public boolean isBackward_bottom_field_reference() {
		return backward_bottom_field_reference;
	}

	public void setBackward_bottom_field_reference(
			boolean backward_bottom_field_reference) {
		this.backward_bottom_field_reference = backward_bottom_field_reference;
	}

	public short getBackward_shape_height() {
		return backward_shape_height;
	}

	public void setBackward_shape_height(short backward_shape_height) {
		this.backward_shape_height = backward_shape_height;
	}

	public short getBackward_shape_horizontal_mc_spatial_ref() {
		return backward_shape_horizontal_mc_spatial_ref;
	}

	public void setBackward_shape_horizontal_mc_spatial_ref(
			short backward_shape_horizontal_mc_spatial_ref) {
		this.backward_shape_horizontal_mc_spatial_ref = backward_shape_horizontal_mc_spatial_ref;
	}

	public short getBackward_shape_vertical_mc_spatial_ref() {
		return backward_shape_vertical_mc_spatial_ref;
	}

	public void setBackward_shape_vertical_mc_spatial_ref(
			short backward_shape_vertical_mc_spatial_ref) {
		this.backward_shape_vertical_mc_spatial_ref = backward_shape_vertical_mc_spatial_ref;
	}

	public short getBackward_shape_width() {
		return backward_shape_width;
	}

	public void setBackward_shape_width(short backward_shape_width) {
		this.backward_shape_width = backward_shape_width;
	}

	public boolean isBackward_top_field_reference() {
		return backward_top_field_reference;
	}

	public void setBackward_top_field_reference(
			boolean backward_top_field_reference) {
		this.backward_top_field_reference = backward_top_field_reference;
	}

	public byte getBits_per_pixel() {
		return bits_per_pixel;
	}

	public void setBits_per_pixel(byte bits_per_pixel) {
		this.bits_per_pixel = bits_per_pixel;
	}

	public int getBlock_count() {
		return block_count;
	}

	public void setBlock_count(int block_count) {
		this.block_count = block_count;
	}

	public boolean isBroken_link() {
		return broken_link;
	}

	public void setBroken_link(boolean broken_link) {
		this.broken_link = broken_link;
	}

	public int getCbpc() {
		return cbpc;
	}

	public void setCbpc(int cbpc) {
		this.cbpc = cbpc;
	}

	public byte getCbpy() {
		return cbpy;
	}

	public void setCbpy(byte cbpy) {
		this.cbpy = cbpy;
	}

	public boolean isChange_conv_ratio_disable() {
		return change_conv_ratio_disable;
	}

	public void setChange_conv_ratio_disable(boolean change_conv_ratio_disable) {
		this.change_conv_ratio_disable = change_conv_ratio_disable;
	}

	public byte getChroma_format() {
		return chroma_format;
	}

	public void setChroma_format(byte chroma_format) {
		this.chroma_format = chroma_format;
	}

	public boolean isClosed_gov() {
		return closed_gov;
	}

	public void setClosed_gov(boolean closed_gov) {
		this.closed_gov = closed_gov;
	}

	public boolean isColour_description() {
		return colour_description;
	}

	public void setColour_description(boolean colour_description) {
		this.colour_description = colour_description;
	}

	public boolean isComplexity_estimation_disable() {
		return complexity_estimation_disable;
	}

	public void setComplexity_estimation_disable(
			boolean complexity_estimation_disable) {
		this.complexity_estimation_disable = complexity_estimation_disable;
	}

	public VideoFrame getCurrentFrame() {
		return currentFrame;
	}

	public void setCurrentFrame(VideoFrame currentFrame) {
		this.currentFrame = currentFrame;
	}

	public boolean isData_partitioned() {
		return data_partitioned;
	}

	public void setData_partitioned(boolean data_partitioned) {
		this.data_partitioned = data_partitioned;
	}

	public int getDc_scaler() {
		return dc_scaler;
	}

	public void setDc_scaler(int dc_scaler) {
		this.dc_scaler = dc_scaler;
	}

	public short getDcecs_apm() {
		return dcecs_apm;
	}

	public void setDcecs_apm(short dcecs_apm) {
		this.dcecs_apm = dcecs_apm;
	}

	public short getDcecs_dct_coefs() {
		return dcecs_dct_coefs;
	}

	public void setDcecs_dct_coefs(short dcecs_dct_coefs) {
		this.dcecs_dct_coefs = dcecs_dct_coefs;
	}

	public short getDcecs_dct_lines() {
		return dcecs_dct_lines;
	}

	public void setDcecs_dct_lines(short dcecs_dct_lines) {
		this.dcecs_dct_lines = dcecs_dct_lines;
	}

	public short getDcecs_forw_back_mc_q() {
		return dcecs_forw_back_mc_q;
	}

	public void setDcecs_forw_back_mc_q(short dcecs_forw_back_mc_q) {
		this.dcecs_forw_back_mc_q = dcecs_forw_back_mc_q;
	}

	public short getDcecs_halfpel2() {
		return dcecs_halfpel2;
	}

	public void setDcecs_halfpel2(short dcecs_halfpel2) {
		this.dcecs_halfpel2 = dcecs_halfpel2;
	}

	public short getDcecs_halfpel4() {
		return dcecs_halfpel4;
	}

	public void setDcecs_halfpel4(short dcecs_halfpel4) {
		this.dcecs_halfpel4 = dcecs_halfpel4;
	}

	public short getDcecs_inter_blocks() {
		return dcecs_inter_blocks;
	}

	public void setDcecs_inter_blocks(short dcecs_inter_blocks) {
		this.dcecs_inter_blocks = dcecs_inter_blocks;
	}

	public short getDcecs_inter_cae() {
		return dcecs_inter_cae;
	}

	public void setDcecs_inter_cae(short dcecs_inter_cae) {
		this.dcecs_inter_cae = dcecs_inter_cae;
	}

	public short getDcecs_inter4v_blocks() {
		return dcecs_inter4v_blocks;
	}

	public void setDcecs_inter4v_blocks(short dcecs_inter4v_blocks) {
		this.dcecs_inter4v_blocks = dcecs_inter4v_blocks;
	}

	public short getDcecs_interpolate_mc_q() {
		return dcecs_interpolate_mc_q;
	}

	public void setDcecs_interpolate_mc_q(short dcecs_interpolate_mc_q) {
		this.dcecs_interpolate_mc_q = dcecs_interpolate_mc_q;
	}

	public short getDcecs_intra_blocks() {
		return dcecs_intra_blocks;
	}

	public void setDcecs_intra_blocks(short dcecs_intra_blocks) {
		this.dcecs_intra_blocks = dcecs_intra_blocks;
	}

	public short getDcecs_intra_cae() {
		return dcecs_intra_cae;
	}

	public void setDcecs_intra_cae(short dcecs_intra_cae) {
		this.dcecs_intra_cae = dcecs_intra_cae;
	}

	public short getDcecs_no_update() {
		return dcecs_no_update;
	}

	public void setDcecs_no_update(short dcecs_no_update) {
		this.dcecs_no_update = dcecs_no_update;
	}

	public short getDcecs_not_coded_blocks() {
		return dcecs_not_coded_blocks;
	}

	public void setDcecs_not_coded_blocks(short dcecs_not_coded_blocks) {
		this.dcecs_not_coded_blocks = dcecs_not_coded_blocks;
	}

	public short getDcecs_npm() {
		return dcecs_npm;
	}

	public void setDcecs_npm(short dcecs_npm) {
		this.dcecs_npm = dcecs_npm;
	}

	public short getDcecs_opaque() {
		return dcecs_opaque;
	}

	public void setDcecs_opaque(short dcecs_opaque) {
		this.dcecs_opaque = dcecs_opaque;
	}

	public short getDcecs_quarterpel() {
		return dcecs_quarterpel;
	}

	public void setDcecs_quarterpel(short dcecs_quarterpel) {
		this.dcecs_quarterpel = dcecs_quarterpel;
	}

	public short getDcecs_sadct() {
		return dcecs_sadct;
	}

	public void setDcecs_sadct(short dcecs_sadct) {
		this.dcecs_sadct = dcecs_sadct;
	}

	public short getDcecs_transparent() {
		return dcecs_transparent;
	}

	public void setDcecs_transparent(short dcecs_transparent) {
		this.dcecs_transparent = dcecs_transparent;
	}

	public short getDcecs_upsampling() {
		return dcecs_upsampling;
	}

	public void setDcecs_upsampling(short dcecs_upsampling) {
		this.dcecs_upsampling = dcecs_upsampling;
	}

	public byte getDcecs_vlc_bits() {
		return dcecs_vlc_bits;
	}

	public void setDcecs_vlc_bits(byte dcecs_vlc_bits) {
		this.dcecs_vlc_bits = dcecs_vlc_bits;
	}

	public short getDcecs_vlc_symbols() {
		return dcecs_vlc_symbols;
	}

	public void setDcecs_vlc_symbols(short dcecs_vlc_symbols) {
		this.dcecs_vlc_symbols = dcecs_vlc_symbols;
	}

	public int[] getDct_coeff() {
		return dct_coeff;
	}

	public void setDct_coeff(int[] dct_coeff) {
		this.dct_coeff = dct_coeff;
	}

	public boolean isDct_coefs() {
		return dct_coefs;
	}

	public void setDct_coefs(boolean dct_coefs) {
		this.dct_coefs = dct_coefs;
	}

	public int getDct_dc_differential() {
		return dct_dc_differential;
	}

	public void setDct_dc_differential(int dct_dc_differential) {
		this.dct_dc_differential = dct_dc_differential;
	}

	public int getDct_dc_size_chrominance() {
		return dct_dc_size_chrominance;
	}

	public void setDct_dc_size_chrominance(int dct_dc_size_chrominance) {
		this.dct_dc_size_chrominance = dct_dc_size_chrominance;
	}

	public int getDct_dc_size_luminance() {
		return dct_dc_size_luminance;
	}

	public void setDct_dc_size_luminance(int dct_dc_size_luminance) {
		this.dct_dc_size_luminance = dct_dc_size_luminance;
	}

	public boolean isDct_lines() {
		return dct_lines;
	}

	public void setDct_lines(boolean dct_lines) {
		this.dct_lines = dct_lines;
	}

	public boolean isDct_type() {
		return dct_type;
	}

	public void setDct_type(boolean dct_type) {
		this.dct_type = dct_type;
	}

	public int getDerived_mb_type() {
		return derived_mb_type;
	}

	public void setDerived_mb_type(int derived_mb_type) {
		this.derived_mb_type = derived_mb_type;
	}

	public byte getDquant() {
		return dquant;
	}

	public void setDquant(byte dquant) {
		this.dquant = dquant;
	}

	public boolean isEnhancement_type() {
		return enhancement_type;
	}

	public void setEnhancement_type(boolean enhancement_type) {
		this.enhancement_type = enhancement_type;
	}

	public byte getEstimation_method() {
		return estimation_method;
	}

	public void setEstimation_method(byte estimation_method) {
		this.estimation_method = estimation_method;
	}

	public boolean isField_prediction() {
		return field_prediction;
	}

	public void setField_prediction(boolean field_prediction) {
		this.field_prediction = field_prediction;
	}

	public short getFirst_half_bit_rate() {
		return first_half_bit_rate;
	}

	public void setFirst_half_bit_rate(short first_half_bit_rate) {
		this.first_half_bit_rate = first_half_bit_rate;
	}

	public short getFirst_half_vbv_buffer_size() {
		return first_half_vbv_buffer_size;
	}

	public void setFirst_half_vbv_buffer_size(short first_half_vbv_buffer_size) {
		this.first_half_vbv_buffer_size = first_half_vbv_buffer_size;
	}

	public short getFirst_half_vbv_occupancy() {
		return first_half_vbv_occupancy;
	}

	public void setFirst_half_vbv_occupancy(short first_half_vbv_occupancy) {
		this.first_half_vbv_occupancy = first_half_vbv_occupancy;
	}

	public boolean isFixed_vop_rate() {
		return fixed_vop_rate;
	}

	public void setFixed_vop_rate(boolean fixed_vop_rate) {
		this.fixed_vop_rate = fixed_vop_rate;
	}

	public int getFixed_vop_time_increment() {
		return fixed_vop_time_increment;
	}

	public void setFixed_vop_time_increment(int fixed_vop_time_increment) {
		this.fixed_vop_time_increment = fixed_vop_time_increment;
	}

	public boolean isForw_back_mc_q() {
		return forw_back_mc_q;
	}

	public void setForw_back_mc_q(boolean forw_back_mc_q) {
		this.forw_back_mc_q = forw_back_mc_q;
	}

	public boolean isForward_bottom_field_reference() {
		return forward_bottom_field_reference;
	}

	public void setForward_bottom_field_reference(
			boolean forward_bottom_field_reference) {
		this.forward_bottom_field_reference = forward_bottom_field_reference;
	}

	public short getForward_shape_height() {
		return forward_shape_height;
	}

	public void setForward_shape_height(short forward_shape_height) {
		this.forward_shape_height = forward_shape_height;
	}

	public short getForward_shape_horizontal_mc_spatial_ref() {
		return forward_shape_horizontal_mc_spatial_ref;
	}

	public void setForward_shape_horizontal_mc_spatial_ref(
			short forward_shape_horizontal_mc_spatial_ref) {
		this.forward_shape_horizontal_mc_spatial_ref = forward_shape_horizontal_mc_spatial_ref;
	}

	public short getForward_shape_vertical_mc_spatial_ref() {
		return forward_shape_vertical_mc_spatial_ref;
	}

	public void setForward_shape_vertical_mc_spatial_ref(
			short forward_shape_vertical_mc_spatial_ref) {
		this.forward_shape_vertical_mc_spatial_ref = forward_shape_vertical_mc_spatial_ref;
	}

	public short getForward_shape_width() {
		return forward_shape_width;
	}

	public void setForward_shape_width(short forward_shape_width) {
		this.forward_shape_width = forward_shape_width;
	}

	public boolean isForward_top_field_reference() {
		return forward_top_field_reference;
	}

	public void setForward_top_field_reference(
			boolean forward_top_field_reference) {
		this.forward_top_field_reference = forward_top_field_reference;
	}

	public boolean isHalfpel2() {
		return halfpel2;
	}

	public void setHalfpel2(boolean halfpel2) {
		this.halfpel2 = halfpel2;
	}

	public boolean isHalfpel4() {
		return halfpel4;
	}

	public void setHalfpel4(boolean halfpel4) {
		this.halfpel4 = halfpel4;
	}

	public boolean isHeader_extension_code() {
		return header_extension_code;
	}

	public void setHeader_extension_code(boolean header_extension_code) {
		this.header_extension_code = header_extension_code;
	}

	public boolean isHierarchy_type() {
		return hierarchy_type;
	}

	public void setHierarchy_type(boolean hierarchy_type) {
		this.hierarchy_type = hierarchy_type;
	}

	public byte getHor_sampling_factor_m() {
		return hor_sampling_factor_m;
	}

	public void setHor_sampling_factor_m(byte hor_sampling_factor_m) {
		this.hor_sampling_factor_m = hor_sampling_factor_m;
	}

	public byte getHor_sampling_factor_n() {
		return hor_sampling_factor_n;
	}

	public void setHor_sampling_factor_n(byte hor_sampling_factor_n) {
		this.hor_sampling_factor_n = hor_sampling_factor_n;
	}

	public int getHorizontal_mv_data() {
		return horizontal_mv_data;
	}

	public void setHorizontal_mv_data(int horizontal_mv_data) {
		this.horizontal_mv_data = horizontal_mv_data;
	}

	public byte getHorizontal_mv_residual() {
		return horizontal_mv_residual;
	}

	public void setHorizontal_mv_residual(byte horizontal_mv_residual) {
		this.horizontal_mv_residual = horizontal_mv_residual;
	}

	public Huffman getHuffman() {
		return huffman;
	}

	public void setHuffman(Huffman huffman) {
		this.huffman = huffman;
	}

	public boolean isInter_blocks() {
		return inter_blocks;
	}

	public void setInter_blocks(boolean inter_blocks) {
		this.inter_blocks = inter_blocks;
	}

	public boolean isInter_cae() {
		return inter_cae;
	}

	public void setInter_cae(boolean inter_cae) {
		this.inter_cae = inter_cae;
	}

	public boolean isInter4v_blocks() {
		return inter4v_blocks;
	}

	public void setInter4v_blocks(boolean inter4v_blocks) {
		this.inter4v_blocks = inter4v_blocks;
	}

	public boolean isInterlaced() {
		return interlaced;
	}

	public void setInterlaced(boolean interlaced) {
		this.interlaced = interlaced;
	}

	public boolean isInterpolate_mc_q() {
		return interpolate_mc_q;
	}

	public void setInterpolate_mc_q(boolean interpolate_mc_q) {
		this.interpolate_mc_q = interpolate_mc_q;
	}

	public boolean isIntra_blocks() {
		return intra_blocks;
	}

	public void setIntra_blocks(boolean intra_blocks) {
		this.intra_blocks = intra_blocks;
	}

	public boolean isIntra_cae() {
		return intra_cae;
	}

	public void setIntra_cae(boolean intra_cae) {
		this.intra_cae = intra_cae;
	}

	public int getIntra_dc_coefficient() {
		return intra_dc_coefficient;
	}

	public void setIntra_dc_coefficient(int intra_dc_coefficient) {
		this.intra_dc_coefficient = intra_dc_coefficient;
	}

	public byte getIntra_dc_vlc_thr() {
		return intra_dc_vlc_thr;
	}

	public void setIntra_dc_vlc_thr(byte intra_dc_vlc_thr) {
		this.intra_dc_vlc_thr = intra_dc_vlc_thr;
	}

	public int[] getIntra_quant_mat() {
		return intra_quant_mat;
	}

	public void setIntra_quant_mat(int[] intra_quant_mat) {
		this.intra_quant_mat = intra_quant_mat;
	}

	public boolean isIs_object_layer_identifier() {
		return is_object_layer_identifier;
	}

	public void setIs_object_layer_identifier(boolean is_object_layer_identifier) {
		this.is_object_layer_identifier = is_object_layer_identifier;
	}

	public boolean isIs_visual_object_identifier() {
		return is_visual_object_identifier;
	}

	public void setIs_visual_object_identifier(
			boolean is_visual_object_identifier) {
		this.is_visual_object_identifier = is_visual_object_identifier;
	}

	public VideoFrame getLast_I_P_Frame() {
		return last_I_P_Frame;
	}

	public void setLast_I_P_Frame(VideoFrame last_I_P_Frame) {
		this.last_I_P_Frame = last_I_P_Frame;
	}

	public short getLatter_half_bit_rate() {
		return latter_half_bit_rate;
	}

	public void setLatter_half_bit_rate(short latter_half_bit_rate) {
		this.latter_half_bit_rate = latter_half_bit_rate;
	}

	public byte getLatter_half_vbv_buffer_size() {
		return latter_half_vbv_buffer_size;
	}

	public void setLatter_half_vbv_buffer_size(byte latter_half_vbv_buffer_size) {
		this.latter_half_vbv_buffer_size = latter_half_vbv_buffer_size;
	}

	public short getLatter_half_vbv_occupancy() {
		return latter_half_vbv_occupancy;
	}

	public void setLatter_half_vbv_occupancy(short latter_half_vbv_occupancy) {
		this.latter_half_vbv_occupancy = latter_half_vbv_occupancy;
	}

	public boolean isLoad_backward_shape() {
		return load_backward_shape;
	}

	public void setLoad_backward_shape(boolean load_backward_shape) {
		this.load_backward_shape = load_backward_shape;
	}

	public boolean isLoad_forward_shape() {
		return load_forward_shape;
	}

	public void setLoad_forward_shape(boolean load_forward_shape) {
		this.load_forward_shape = load_forward_shape;
	}

	public boolean isLoad_intra_quant_mat() {
		return load_intra_quant_mat;
	}

	public void setLoad_intra_quant_mat(boolean load_intra_quant_mat) {
		this.load_intra_quant_mat = load_intra_quant_mat;
	}

	public boolean isLoad_intra_quant_mat_grayscale() {
		return load_intra_quant_mat_grayscale;
	}

	public void setLoad_intra_quant_mat_grayscale(
			boolean load_intra_quant_mat_grayscale) {
		this.load_intra_quant_mat_grayscale = load_intra_quant_mat_grayscale;
	}

	public boolean isLoad_nonintra_quant_mat() {
		return load_nonintra_quant_mat;
	}

	public void setLoad_nonintra_quant_mat(boolean load_nonintra_quant_mat) {
		this.load_nonintra_quant_mat = load_nonintra_quant_mat;
	}

	public boolean isLoad_nonintra_quant_mat_grayscale() {
		return load_nonintra_quant_mat_grayscale;
	}

	public void setLoad_nonintra_quant_mat_grayscale(
			boolean load_nonintra_quant_mat_grayscale) {
		this.load_nonintra_quant_mat_grayscale = load_nonintra_quant_mat_grayscale;
	}

	public boolean isLow_delay() {
		return low_delay;
	}

	public void setLow_delay(boolean low_delay) {
		this.low_delay = low_delay;
	}

	public boolean isLow_latency_sprite_enable() {
		return low_latency_sprite_enable;
	}

	public void setLow_latency_sprite_enable(boolean low_latency_sprite_enable) {
		this.low_latency_sprite_enable = low_latency_sprite_enable;
	}

	public boolean isMacroblock_intra() {
		return macroblock_intra;
	}

	public void setMacroblock_intra(boolean macroblock_intra) {
		this.macroblock_intra = macroblock_intra;
	}

	public short getMacroblock_number() {
		return macroblock_number;
	}

	public void setMacroblock_number(short macroblock_number) {
		this.macroblock_number = macroblock_number;
	}

	public int getMacroblock_number_length() {
		return macroblock_number_length;
	}

	public void setMacroblock_number_length(int macroblock_number_length) {
		this.macroblock_number_length = macroblock_number_length;
	}

	public int getMax_macroblock_number() {
		return max_macroblock_number;
	}

	public void setMax_macroblock_number(int max_macroblock_number) {
		this.max_macroblock_number = max_macroblock_number;
	}

	public int getMb_type() {
		return mb_type;
	}

	public void setMb_type(int mb_type) {
		this.mb_type = mb_type;
	}

	public boolean isMcsel() {
		return mcsel;
	}

	public void setMcsel(boolean mcsel) {
		this.mcsel = mcsel;
	}

	public int getModulo_time_base() {
		return modulo_time_base;
	}

	public void setModulo_time_base(int modulo_time_base) {
		this.modulo_time_base = modulo_time_base;
	}

	public boolean isMotion_compensation_complexity_disable() {
		return motion_compensation_complexity_disable;
	}

	public void setMotion_compensation_complexity_disable(
			boolean motion_compensation_complexity_disable) {
		this.motion_compensation_complexity_disable = motion_compensation_complexity_disable;
	}

	public boolean isNewpred_enable() {
		return newpred_enable;
	}

	public void setNewpred_enable(boolean newpred_enable) {
		this.newpred_enable = newpred_enable;
	}

	public boolean isNewpred_segment_type() {
		return newpred_segment_type;
	}

	public void setNewpred_segment_type(boolean newpred_segment_type) {
		this.newpred_segment_type = newpred_segment_type;
	}

	public byte getNo_of_sprite_warping_points() {
		return no_of_sprite_warping_points;
	}

	public void setNo_of_sprite_warping_points(byte no_of_sprite_warping_points) {
		this.no_of_sprite_warping_points = no_of_sprite_warping_points;
	}

	public boolean isNo_update() {
		return no_update;
	}

	public void setNo_update(boolean no_update) {
		this.no_update = no_update;
	}

	public int[] getNonintra_quant_mat() {
		return nonintra_quant_mat;
	}

	public void setNonintra_quant_mat(int[] nonintra_quant_mat) {
		this.nonintra_quant_mat = nonintra_quant_mat;
	}

	public boolean isNot_8_bit() {
		return not_8_bit;
	}

	public void setNot_8_bit(boolean not_8_bit) {
		this.not_8_bit = not_8_bit;
	}

	public boolean isNot_coded() {
		return not_coded;
	}

	public void setNot_coded(boolean not_coded) {
		this.not_coded = not_coded;
	}

	public boolean isNot_coded_blocks() {
		return not_coded_blocks;
	}

	public void setNot_coded_blocks(boolean not_coded_blocks) {
		this.not_coded_blocks = not_coded_blocks;
	}

	public boolean isNpm() {
		return npm;
	}

	public void setNpm(boolean npm) {
		this.npm = npm;
	}

	public boolean isObmc_disable() {
		return obmc_disable;
	}

	public void setObmc_disable(boolean obmc_disable) {
		this.obmc_disable = obmc_disable;
	}

	public boolean isOpaque() {
		return opaque;
	}

	public void setOpaque(boolean opaque) {
		this.opaque = opaque;
	}

	public short getPar_height() {
		return par_height;
	}

	public void setPar_height(short par_height) {
		this.par_height = par_height;
	}

	public short getPar_width() {
		return par_width;
	}

	public void setPar_width(short par_width) {
		this.par_width = par_width;
	}

	public int getPrev_vop_time_increment() {
		return prev_vop_time_increment;
	}

	public void setPrev_vop_time_increment(int prev_vop_time_increment) {
		this.prev_vop_time_increment = prev_vop_time_increment;
	}

	public short getPrevQp() {
		return prevQp;
	}

	public void setPrevQp(short prevQp) {
		this.prevQp = prevQp;
	}

	public boolean isPrinted_video_info() {
		return printed_video_info;
	}

	public void setPrinted_video_info(boolean printed_video_info) {
		this.printed_video_info = printed_video_info;
	}

	public short getProfile_and_level_indication() {
		return profile_and_level_indication;
	}

	public void setProfile_and_level_indication(
			short profile_and_level_indication) {
		this.profile_and_level_indication = profile_and_level_indication;
	}

	public short getQp() {
		return Qp;
	}

	public void setQp(short qp) {
		Qp = qp;
	}

	public byte getQuant_precision() {
		return quant_precision;
	}

	public void setQuant_precision(byte quant_precision) {
		this.quant_precision = quant_precision;
	}

	public short getQuant_scale() {
		return quant_scale;
	}

	public void setQuant_scale(short quant_scale) {
		this.quant_scale = quant_scale;
	}

	public byte getQuant_type() {
		return quant_type;
	}

	public void setQuant_type(byte quant_type) {
		this.quant_type = quant_type;
	}

	public short getQuantiser_scale() {
		return quantiser_scale;
	}

	public void setQuantiser_scale(short quantiser_scale) {
		this.quantiser_scale = quantiser_scale;
	}

	public boolean isQuarter_sample() {
		return quarter_sample;
	}

	public void setQuarter_sample(boolean quarter_sample) {
		this.quarter_sample = quarter_sample;
	}

	public boolean isQuarterpel() {
		return quarterpel;
	}

	public void setQuarterpel(boolean quarterpel) {
		this.quarterpel = quarterpel;
	}

	public boolean isRandom_accessible_vol() {
		return random_accessible_vol;
	}

	public void setRandom_accessible_vol(boolean random_accessible_vol) {
		this.random_accessible_vol = random_accessible_vol;
	}

	public boolean isReduced_resolution_vop_enable() {
		return reduced_resolution_vop_enable;
	}

	public void setReduced_resolution_vop_enable(
			boolean reduced_resolution_vop_enable) {
		this.reduced_resolution_vop_enable = reduced_resolution_vop_enable;
	}

	public byte getRef_layer_id() {
		return ref_layer_id;
	}

	public void setRef_layer_id(byte ref_layer_id) {
		this.ref_layer_id = ref_layer_id;
	}

	public boolean isRef_layer_sampling_direc() {
		return ref_layer_sampling_direc;
	}

	public void setRef_layer_sampling_direc(boolean ref_layer_sampling_direc) {
		this.ref_layer_sampling_direc = ref_layer_sampling_direc;
	}

	public byte getRef_select_code() {
		return ref_select_code;
	}

	public void setRef_select_code(byte ref_select_code) {
		this.ref_select_code = ref_select_code;
	}

	public byte getRequested_upstream_message_type() {
		return requested_upstream_message_type;
	}

	public void setRequested_upstream_message_type(
			byte requested_upstream_message_type) {
		this.requested_upstream_message_type = requested_upstream_message_type;
	}

	public boolean isResync_marker_disable() {
		return resync_marker_disable;
	}

	public void setResync_marker_disable(boolean resync_marker_disable) {
		this.resync_marker_disable = resync_marker_disable;
	}

	public boolean isReversible_vlc() {
		return reversible_vlc;
	}

	public void setReversible_vlc(boolean reversible_vlc) {
		this.reversible_vlc = reversible_vlc;
	}

	public boolean isSadct() {
		return sadct;
	}

	public void setSadct(boolean sadct) {
		this.sadct = sadct;
	}

	public boolean isSadct_disable() {
		return sadct_disable;
	}

	public void setSadct_disable(boolean sadct_disable) {
		this.sadct_disable = sadct_disable;
	}

	public boolean isScalability() {
		return scalability;
	}

	public void setScalability(boolean scalability) {
		this.scalability = scalability;
	}

	public boolean isShape_complexity_estimation_disable() {
		return shape_complexity_estimation_disable;
	}

	public void setShape_complexity_estimation_disable(
			boolean shape_complexity_estimation_disable) {
		this.shape_complexity_estimation_disable = shape_complexity_estimation_disable;
	}

	public byte getShape_hor_sampling_factor_m() {
		return shape_hor_sampling_factor_m;
	}

	public void setShape_hor_sampling_factor_m(byte shape_hor_sampling_factor_m) {
		this.shape_hor_sampling_factor_m = shape_hor_sampling_factor_m;
	}

	public byte getShape_hor_sampling_factor_n() {
		return shape_hor_sampling_factor_n;
	}

	public void setShape_hor_sampling_factor_n(byte shape_hor_sampling_factor_n) {
		this.shape_hor_sampling_factor_n = shape_hor_sampling_factor_n;
	}

	public byte getShape_vert_sampling_factor_m() {
		return shape_vert_sampling_factor_m;
	}

	public void setShape_vert_sampling_factor_m(
			byte shape_vert_sampling_factor_m) {
		this.shape_vert_sampling_factor_m = shape_vert_sampling_factor_m;
	}

	public byte getShape_vert_sampling_factor_n() {
		return shape_vert_sampling_factor_n;
	}

	public void setShape_vert_sampling_factor_n(
			byte shape_vert_sampling_factor_n) {
		this.shape_vert_sampling_factor_n = shape_vert_sampling_factor_n;
	}

	public boolean isShort_video_header() {
		return short_video_header;
	}

	public void setShort_video_header(boolean short_video_header) {
		this.short_video_header = short_video_header;
	}

	public byte getSprite_enable() {
		return sprite_enable;
	}

	public void setSprite_enable(byte sprite_enable) {
		this.sprite_enable = sprite_enable;
	}

	public byte getSprite_transmit_mode() {
		return sprite_transmit_mode;
	}

	public void setSprite_transmit_mode(byte sprite_transmit_mode) {
		this.sprite_transmit_mode = sprite_transmit_mode;
	}

	public boolean isTexture_complexity_estimation_set_1_disable() {
		return texture_complexity_estimation_set_1_disable;
	}

	public void setTexture_complexity_estimation_set_1_disable(
			boolean texture_complexity_estimation_set_1_disable) {
		this.texture_complexity_estimation_set_1_disable = texture_complexity_estimation_set_1_disable;
	}

	public boolean isTexture_complexity_estimation_set_2_disable() {
		return texture_complexity_estimation_set_2_disable;
	}

	public void setTexture_complexity_estimation_set_2_disable(
			boolean texture_complexity_estimation_set_2_disable) {
		this.texture_complexity_estimation_set_2_disable = texture_complexity_estimation_set_2_disable;
	}

	public int getTime_code_hours() {
		return time_code_hours;
	}

	public void setTime_code_hours(int time_code_hours) {
		this.time_code_hours = time_code_hours;
	}

	public int getTime_code_minutes() {
		return time_code_minutes;
	}

	public void setTime_code_minutes(int time_code_minutes) {
		this.time_code_minutes = time_code_minutes;
	}

	public int getTime_code_seconds() {
		return time_code_seconds;
	}

	public void setTime_code_seconds(int time_code_seconds) {
		this.time_code_seconds = time_code_seconds;
	}

	public boolean isTop_field_first() {
		return top_field_first;
	}

	public void setTop_field_first(boolean top_field_first) {
		this.top_field_first = top_field_first;
	}

	public boolean isTransparent() {
		return transparent;
	}

	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
	}

	public boolean isUpsampling() {
		return upsampling;
	}

	public void setUpsampling(boolean upsampling) {
		this.upsampling = upsampling;
	}

	public boolean isUse_intra_dc_vlc() {
		return use_intra_dc_vlc;
	}

	public void setUse_intra_dc_vlc(boolean use_intra_dc_vlc) {
		this.use_intra_dc_vlc = use_intra_dc_vlc;
	}

	public boolean isUse_ref_shape() {
		return use_ref_shape;
	}

	public void setUse_ref_shape(boolean use_ref_shape) {
		this.use_ref_shape = use_ref_shape;
	}

	public boolean isUse_ref_texture() {
		return use_ref_texture;
	}

	public void setUse_ref_texture(boolean use_ref_texture) {
		this.use_ref_texture = use_ref_texture;
	}

	public boolean isVbv_parameters() {
		return vbv_parameters;
	}

	public void setVbv_parameters(boolean vbv_parameters) {
		this.vbv_parameters = vbv_parameters;
	}

	public boolean isVersion2_complexity_estimation_disable() {
		return version2_complexity_estimation_disable;
	}

	public void setVersion2_complexity_estimation_disable(
			boolean version2_complexity_estimation_disable) {
		this.version2_complexity_estimation_disable = version2_complexity_estimation_disable;
	}

	public byte getVert_sampling_factor_m() {
		return vert_sampling_factor_m;
	}

	public void setVert_sampling_factor_m(byte vert_sampling_factor_m) {
		this.vert_sampling_factor_m = vert_sampling_factor_m;
	}

	public byte getVert_sampling_factor_n() {
		return vert_sampling_factor_n;
	}

	public void setVert_sampling_factor_n(byte vert_sampling_factor_n) {
		this.vert_sampling_factor_n = vert_sampling_factor_n;
	}

	public int getVertical_mv_data() {
		return vertical_mv_data;
	}

	public void setVertical_mv_data(int vertical_mv_data) {
		this.vertical_mv_data = vertical_mv_data;
	}

	public byte getVertical_mv_residual() {
		return vertical_mv_residual;
	}

	public void setVertical_mv_residual(byte vertical_mv_residual) {
		this.vertical_mv_residual = vertical_mv_residual;
	}

	public boolean isVertical_prediction() {
		return vertical_prediction;
	}

	public void setVertical_prediction(boolean vertical_prediction) {
		this.vertical_prediction = vertical_prediction;
	}

	public short getVideo_object_layer_height() {
		return video_object_layer_height;
	}

	public void setVideo_object_layer_height(short video_object_layer_height) {
		this.video_object_layer_height = video_object_layer_height;
	}

	public byte getVideo_object_layer_id() {
		return video_object_layer_id;
	}

	public void setVideo_object_layer_id(byte video_object_layer_id) {
		this.video_object_layer_id = video_object_layer_id;
	}

	public byte getVideo_object_layer_priority() {
		return video_object_layer_priority;
	}

	public void setVideo_object_layer_priority(byte video_object_layer_priority) {
		this.video_object_layer_priority = video_object_layer_priority;
	}

	public byte getVideo_object_layer_shape() {
		return video_object_layer_shape;
	}

	public void setVideo_object_layer_shape(byte video_object_layer_shape) {
		this.video_object_layer_shape = video_object_layer_shape;
	}

	public byte getVideo_object_layer_shape_extension() {
		return video_object_layer_shape_extension;
	}

	public void setVideo_object_layer_shape_extension(
			byte video_object_layer_shape_extension) {
		this.video_object_layer_shape_extension = video_object_layer_shape_extension;
	}

	public byte getVideo_object_layer_verid() {
		return video_object_layer_verid;
	}

	public void setVideo_object_layer_verid(byte video_object_layer_verid) {
		this.video_object_layer_verid = video_object_layer_verid;
	}

	public short getVideo_object_layer_width() {
		return video_object_layer_width;
	}

	public void setVideo_object_layer_width(short video_object_layer_width) {
		this.video_object_layer_width = video_object_layer_width;
	}

	public short getVideo_object_type_indication() {
		return video_object_type_indication;
	}

	public void setVideo_object_type_indication(
			short video_object_type_indication) {
		this.video_object_type_indication = video_object_type_indication;
	}

	public double getVideo_rate() {
		return video_rate;
	}

	public void setVideo_rate(double video_rate) {
		this.video_rate = video_rate;
	}

	public boolean isVideo_signal_type() {
		return video_signal_type;
	}

	public void setVideo_signal_type(boolean video_signal_type) {
		this.video_signal_type = video_signal_type;
	}

	public int getVideo_size() {
		return video_size;
	}

	public void setVideo_size(int video_size) {
		this.video_size = video_size;
	}

	public BitStream getVideoStream() {
		return videoStream;
	}

	public void setVideoStream(BitStream videoStream) {
		this.videoStream = videoStream;
	}

	public Thread getVideoThread() {
		return videoThread;
	}

	public void setVideoThread(Thread videoThread) {
		this.videoThread = videoThread;
	}

	public byte getVisual_object_type() {
		return visual_object_type;
	}

	public void setVisual_object_type(byte visual_object_type) {
		this.visual_object_type = visual_object_type;
	}

	public boolean isVlc_bits() {
		return vlc_bits;
	}

	public void setVlc_bits(boolean vlc_bits) {
		this.vlc_bits = vlc_bits;
	}

	public boolean isVlc_symbols() {
		return vlc_symbols;
	}

	public void setVlc_symbols(boolean vlc_symbols) {
		this.vlc_symbols = vlc_symbols;
	}

	public boolean isVol_control_parameters() {
		return vol_control_parameters;
	}

	public void setVol_control_parameters(boolean vol_control_parameters) {
		this.vol_control_parameters = vol_control_parameters;
	}

	public boolean isVop_coded() {
		return vop_coded;
	}

	public void setVop_coded(boolean vop_coded) {
		this.vop_coded = vop_coded;
	}

	public int getVop_coding_type() {
		return vop_coding_type;
	}

	public void setVop_coding_type(int vop_coding_type) {
		this.vop_coding_type = vop_coding_type;
	}

	public boolean isVop_constant_alpha() {
		return vop_constant_alpha;
	}

	public void setVop_constant_alpha(boolean vop_constant_alpha) {
		this.vop_constant_alpha = vop_constant_alpha;
	}

	public short getVop_constant_alpha_value() {
		return vop_constant_alpha_value;
	}

	public void setVop_constant_alpha_value(short vop_constant_alpha_value) {
		this.vop_constant_alpha_value = vop_constant_alpha_value;
	}

	public byte getVop_fcode_backward() {
		return vop_fcode_backward;
	}

	public void setVop_fcode_backward(byte vop_fcode_backward) {
		this.vop_fcode_backward = vop_fcode_backward;
	}

	public byte getVop_fcode_forward() {
		return vop_fcode_forward;
	}

	public void setVop_fcode_forward(byte vop_fcode_forward) {
		this.vop_fcode_forward = vop_fcode_forward;
	}

	public short getVop_height() {
		return vop_height;
	}

	public void setVop_height(short vop_height) {
		this.vop_height = vop_height;
	}

	public short getVop_horizontal_mc_spatial_ref() {
		return vop_horizontal_mc_spatial_ref;
	}

	public void setVop_horizontal_mc_spatial_ref(
			short vop_horizontal_mc_spatial_ref) {
		this.vop_horizontal_mc_spatial_ref = vop_horizontal_mc_spatial_ref;
	}

	public short getVop_id() {
		return vop_id;
	}

	public void setVop_id(short vop_id) {
		this.vop_id = vop_id;
	}

	public short getVop_id_for_prediction() {
		return vop_id_for_prediction;
	}

	public void setVop_id_for_prediction(short vop_id_for_prediction) {
		this.vop_id_for_prediction = vop_id_for_prediction;
	}

	public boolean isVop_id_for_prediction_indication() {
		return vop_id_for_prediction_indication;
	}

	public void setVop_id_for_prediction_indication(
			boolean vop_id_for_prediction_indication) {
		this.vop_id_for_prediction_indication = vop_id_for_prediction_indication;
	}

	public int getVop_id_length() {
		return vop_id_length;
	}

	public void setVop_id_length(int vop_id_length) {
		this.vop_id_length = vop_id_length;
	}

	public int getVop_number_in_gop() {
		return vop_number_in_gop;
	}

	public void setVop_number_in_gop(int vop_number_in_gop) {
		this.vop_number_in_gop = vop_number_in_gop;
	}

	public short getVop_quant() {
		return vop_quant;
	}

	public void setVop_quant(short vop_quant) {
		this.vop_quant = vop_quant;
	}

	public boolean isVop_reduced_resolution() {
		return vop_reduced_resolution;
	}

	public void setVop_reduced_resolution(boolean vop_reduced_resolution) {
		this.vop_reduced_resolution = vop_reduced_resolution;
	}

	public byte getVop_rounding_type() {
		return vop_rounding_type;
	}

	public void setVop_rounding_type(byte vop_rounding_type) {
		this.vop_rounding_type = vop_rounding_type;
	}

	public boolean isVop_shape_coding_type() {
		return vop_shape_coding_type;
	}

	public void setVop_shape_coding_type(boolean vop_shape_coding_type) {
		this.vop_shape_coding_type = vop_shape_coding_type;
	}

	public int getVop_time_increment() {
		return vop_time_increment;
	}

	public void setVop_time_increment(int vop_time_increment) {
		this.vop_time_increment = vop_time_increment;
	}

	public int getVop_time_increment_length() {
		return vop_time_increment_length;
	}

	public void setVop_time_increment_length(int vop_time_increment_length) {
		this.vop_time_increment_length = vop_time_increment_length;
	}

	public int getVop_time_increment_resolution() {
		return vop_time_increment_resolution;
	}

	public void setVop_time_increment_resolution(
			int vop_time_increment_resolution) {
		this.vop_time_increment_resolution = vop_time_increment_resolution;
	}

	public short getVop_vertical_mc_spatial_ref() {
		return vop_vertical_mc_spatial_ref;
	}

	public void setVop_vertical_mc_spatial_ref(short vop_vertical_mc_spatial_ref) {
		this.vop_vertical_mc_spatial_ref = vop_vertical_mc_spatial_ref;
	}

	public short getVop_width() {
		return vop_width;
	}

	public void setVop_width(short vop_width) {
		this.vop_width = vop_width;
	}

	public int getDuration() {
		return duration;
	}

	public int getIVideo_rate() {
		return iVideo_rate;
	}

	public int getBaseTime() {
		return base_time;
	}

}
