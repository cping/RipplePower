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

/**
 * based on H.263 decoder, (C) 1995, 1996 Telenor R&D, Norway 
 * 										  Karl Olav Lillevold <Karl.Lillevold@nta.no>
 *
 * modified by Wayne Ellis BT Labs to run Annex E Arithmetic Decoding
 *           <ellis_w_wayne@bt-web.bt.co.uk>
 *
 * based on mpeg2decode, (C) 1994, MPEG Software Simulation Group
 * and mpeg2play, (C) 1994 Stefan Eckart
 *                         <stefan@lis.e-technik.tu-muenchen.de>
 */

/**
 * VideoFrame
 * 
 */
public final class VideoFrame {
	
	private int type;
	
	private int buffer_width;
	private int buffer_height;
	private int frame_width;
	private int frame_height;

	
	private int[][] zero_block = new int[9][9];
	private int[][] zero_block2 = new int[9][9];

	private int[][][] lumminance_blocks;
	private int[][][] cr_chrominace_blocks;
	private int[][][] cb_chrominace_blocks;
	
	private int lumminance_blocks_per_line;
	private int chrominace_blocks_per_line;

	private int previousQuantiserScale;
	private int[][] previousBlock;
	
	private int[][] macroblocks_info;
	
	private IDCT idct; 
	
	private int[][] pixel_data;
	
	private byte rounding_control;
	
	private long playing_time;

	/**
	 * Constructs a VideoFrame object.
	 * @param type
	 * @param width
	 * @param height
	 * @param bits_per_pixel
	 */
	public VideoFrame(int type, int width, int height, int bits_per_pixel) {
		super();
		this.type = type;
		this.frame_width = buffer_width = width;
		this.frame_height = buffer_height = height;
		this.rounding_control = 0;
		
		if((buffer_width & 15) > 0) {
			buffer_width = buffer_width - (buffer_width & 15) + 16; 
		}

		if((buffer_height & 15) > 0) {
			buffer_height = buffer_height - (buffer_height & 15) + 16; 
		}			

		this.idct = new IDCT(buffer_width);
		
		for(int i = 0; i < 9; i ++) {
			for(int j = 0; j < 9; j ++) {
				zero_block[i][j] = 0;
				zero_block2[i][j] = 0;
			}
		}
		// init DC value
		zero_block[0][0] = 1 << (bits_per_pixel + 2);

		this.lumminance_blocks_per_line = buffer_width >> 3;
		this.chrominace_blocks_per_line = buffer_width >> 4;
		this.lumminance_blocks = new int[(buffer_width >> 3) * (buffer_height >> 3)][9][9];
		this.cr_chrominace_blocks = new int[(buffer_width >> 4) * (buffer_height >> 4)][9][9];
		this.cb_chrominace_blocks = new int[(buffer_width >> 4) * (buffer_height >> 4)][9][9];
		this.macroblocks_info = new int[(buffer_width >> 4) * (buffer_height >> 4)][10];
		this.pixel_data = new int[3][buffer_width * buffer_height];
	}

	public void copyMacroblock(VideoFrame last_I_P_Frame, int macroblock_number) {
		// set macroblock type and quantization information 
		macroblocks_info[macroblock_number][0] = 0;
		macroblocks_info[macroblock_number][1] = 0;
		// set motion vector data
		macroblocks_info[macroblock_number][2] = 
			macroblocks_info[macroblock_number][3] = 
			macroblocks_info[macroblock_number][4] = 
			macroblocks_info[macroblock_number][5] = 0;
		macroblocks_info[macroblock_number][6] = 
			macroblocks_info[macroblock_number][7] = 
			macroblocks_info[macroblock_number][8] = 
			macroblocks_info[macroblock_number][9] = 0;

		// copy pixel data
		int[][] src_pixel_data = last_I_P_Frame.getPixelData();
		int x_index_c = macroblock_number % chrominace_blocks_per_line;
		int y_index_c = macroblock_number / chrominace_blocks_per_line;
		// copy lumminance data
		int start_index = ((y_index_c * buffer_width) << 4) + (x_index_c << 4);
		for(int i = 0; i < 16; i++) {
			System.arraycopy(src_pixel_data[0], start_index, pixel_data[0], start_index, 16);
			start_index += buffer_width;
		}
		// copy chrominance data
		start_index = ((y_index_c * buffer_width) << 3) + (x_index_c << 3);
		for(int i = 0; i < 8; i++) {
			System.arraycopy(src_pixel_data[1], start_index, pixel_data[1], start_index, 8);
			System.arraycopy(src_pixel_data[2], start_index, pixel_data[2], start_index, 8);
			start_index += buffer_width;
		}
	}

	private int MVDx;
	private int MVDy;
	
	private int MVx;
	private int MVy;
	
	private int Px;
	private int Py;
	
	private boolean quarter_sample;
	
	int[][][] predictor_indexes = {
		// macroblock with one motion vector
		{  // x  y  n    x   y   n    x   y  n
			{-1, 0, 1}, {0, -1 , 2}, {1, -1, 2} 
		},  
		// macroblock with four motion vectors
		// block (n = 0)
		{  // x  y  n    x   y   n    x   y  n
			{-1, 0, 1}, {0, -1 , 2}, {1, -1, 2} 
		},  
		// block (n = 1)
		{  // x  y  n    x   y   n    x   y  n
			 {0, 0, 0}, {0, -1 , 3}, {1, -1, 2} 
		},  
		// block (n = 2)
		{  // x  y  n    x   y   n    x   y  n
			{-1, 0, 3}, {0,  0 , 0}, {0,  0, 1} 
		},  
		// block (n = 3)
		{  // x  y  n    x   y   n    x   y  n
			 {0, 0, 2}, {0,  0 , 0}, {0,  0, 1} 
		}  
	};
	
	public void setForwardMotionVector(
				int macroblock_number, int block_number, boolean quarter_sample,	int vop_fcode_forward, 
				int horizontal_mv_data, byte horizontal_mv_residual, 
				int vertical_mv_data, byte vertical_mv_residual) {
					
		this.quarter_sample = quarter_sample;

		int x_index = macroblock_number % chrominace_blocks_per_line;
		int y_index = macroblock_number / chrominace_blocks_per_line;
		int max_x = chrominace_blocks_per_line - 1;
		int[][] predictor_index = predictor_indexes[block_number + 1];
		if(block_number != -1) {
			x_index = (x_index << 1) + (block_number & 1);
			y_index = (y_index << 1) + (block_number < 2 ? 0 : 1);
			max_x = (chrominace_blocks_per_line << 1) - 1;
		}

		Px = 0;
		Py = 0;

		if(y_index == 0) {
			if(x_index > 0) {
				Px = macroblocks_info[macroblock_number + predictor_index[0][0]][2 + predictor_index[0][2]];
				Py = macroblocks_info[macroblock_number + predictor_index[0][0]][6 + predictor_index[0][2]];
				if(Px == Integer.MAX_VALUE) {
					Px = 0;
					Py = 0;
				}
			}
		} else {
			int MV1x = Integer.MAX_VALUE;
			int MV2x = Integer.MAX_VALUE;
			int MV3x = Integer.MAX_VALUE;
			int MV1y = Integer.MAX_VALUE;
			int MV2y = Integer.MAX_VALUE;
			int MV3y = Integer.MAX_VALUE;
			MV2x = macroblocks_info[macroblock_number + chrominace_blocks_per_line * predictor_index[1][1]][2 + predictor_index[1][2]];
			MV2y = macroblocks_info[macroblock_number + chrominace_blocks_per_line * predictor_index[1][1]][6 + predictor_index[1][2]];
			if(x_index == 0) {
				MV3x = macroblocks_info[macroblock_number + chrominace_blocks_per_line * predictor_index[2][1] + predictor_index[2][0]][2 + predictor_index[2][2]];
				MV3y = macroblocks_info[macroblock_number + chrominace_blocks_per_line * predictor_index[2][1] + predictor_index[2][0]][6 + predictor_index[2][2]];
			} else if(x_index == max_x) {
				MV1x = macroblocks_info[macroblock_number + predictor_index[0][0]][2 + predictor_index[0][2]];
				MV1y = macroblocks_info[macroblock_number + predictor_index[0][0]][6 + predictor_index[0][2]];
			} else {
				MV1x = macroblocks_info[macroblock_number + predictor_index[0][0]][2 + predictor_index[0][2]];
				MV1y = macroblocks_info[macroblock_number + predictor_index[0][0]][6 + predictor_index[0][2]];
				MV3x = macroblocks_info[macroblock_number + chrominace_blocks_per_line * predictor_index[2][1] + predictor_index[2][0]][2 + predictor_index[2][2]];
				MV3y = macroblocks_info[macroblock_number + chrominace_blocks_per_line * predictor_index[2][1] + predictor_index[2][0]][6 + predictor_index[2][2]];
			}
			int n = 3;
			int mask = 0;
			if(MV1x == Integer.MAX_VALUE) {
				MV1x = MV1y = 0;
				n--;
				mask |= 1;
			}
			if(MV2x == Integer.MAX_VALUE) {
				MV2x = MV2y = 0;
				n--;
				mask |= 2;
			}
			if(MV3x == Integer.MAX_VALUE) {
				MV3x = MV3y = 0;
				n--;
				mask |= 4;
			}
			if(n == 1) {
				if(mask == 6) {
					Px = MV1x;
					Py = MV1y;
				} else if(mask == 5) {
					Px = MV2x;
					Py = MV2y;
				} else if(mask == 3) {
					Px = MV3x;
					Py = MV3y;
				}
			} else if(n > 0) {
				Px = MV1x + MV2x + MV3x - Math.max(MV3x, Math.max(MV1x, MV2x)) - Math.min(MV3x, Math.min(MV1x, MV2x));
				Py = MV1y + MV2y + MV3y - Math.max(MV3y, Math.max(MV1y, MV2y)) - Math.min(MV3y, Math.min(MV1y, MV2y));
			}
		}		
			
		int r_size = vop_fcode_forward - 1;
		int f = 1 << r_size;
		int high = ( 32 * f ) - 1;
		int low = ( (-32) * f );
		int range = ( 64 * f );
		if ( (f == 1) || (horizontal_mv_data == 0) )
			MVDx = horizontal_mv_data;
		else {
			MVDx = ( ( Math.abs(horizontal_mv_data) - 1 ) * f ) + horizontal_mv_residual + 1;
			if (horizontal_mv_data < 0)
				MVDx = - MVDx;
		}
		if ( (f == 1) || (vertical_mv_data == 0) )
			MVDy = vertical_mv_data;
		else {
			MVDy = ( ( Math.abs(vertical_mv_data) - 1 ) * f ) + vertical_mv_residual + 1;
			if (vertical_mv_data < 0)
				MVDy = - MVDy;
		}

		if(quarter_sample) {
			// divides motion vector by 2 - try to emulate quarter sample mode with the half sample mode
			MVDx >>= 1;
			MVDy >>= 1;
			low >>= 1;
			high >>= 1;
		}
			
		MVx = MVDx + Px;
		if ( MVx < low )
			MVx = MVx + range;
		if (MVx > high)
			MVx = MVx - range;
		MVy = MVDy + Py;
		if ( MVy < low )
			MVy = MVy + range;
		if (MVy > high)
			MVy = MVy - range;

		if(block_number == -1) {
			// one motion vector per macroblock
			macroblocks_info[macroblock_number][2] = 
				macroblocks_info[macroblock_number][3] = 
				macroblocks_info[macroblock_number][4] = 
				macroblocks_info[macroblock_number][5] = MVx;
		
			macroblocks_info[macroblock_number][6] = 
				macroblocks_info[macroblock_number][7] = 
				macroblocks_info[macroblock_number][8] = 
				macroblocks_info[macroblock_number][9] = MVy;
		} else {
			// four motion vectors per macroblock
			macroblocks_info[macroblock_number][2 + block_number] = MVx; 
			macroblocks_info[macroblock_number][6 + block_number] = MVy;
		}
	}
	
	private static final int[] roundtab = {0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2};
	
	public void applyForwardMotionVector(VideoFrame last_I_P_Frame, int macroblock_number) {
		int[][] src_pixel_data = last_I_P_Frame.getPixelData();
		int x_index_c = macroblock_number % chrominace_blocks_per_line;
		int y_index_c = macroblock_number / chrominace_blocks_per_line;
		boolean half_x;
		boolean half_y;
		int[] dst_data = null; 
		int[] src_data = null; 
		int src_start_index = 0;
		int dst_start_index = 0;
		int block_width;
		int motion_block_width = 16;
		int motion_vectors = 1;
		int MVCHx = 0;
		int MVCHy = 0;
		if(macroblocks_info[macroblock_number][0] == 2) {
			// four motion vectors per macroblock
			motion_vectors = 4;
			motion_block_width = 8;
			// calculates the chrominance motion vector
			MVCHx = (macroblocks_info[macroblock_number][2] + macroblocks_info[macroblock_number][3] + 
					 macroblocks_info[macroblock_number][4] + macroblocks_info[macroblock_number][5]);
			MVCHy = (macroblocks_info[macroblock_number][6] + macroblocks_info[macroblock_number][7] + 
					 macroblocks_info[macroblock_number][8] + macroblocks_info[macroblock_number][9]);
			int y_sign =((MVCHy >= 0) ? 1 : -1); 
			int x_sign =((MVCHx >= 0) ? 1 : -1);
			int y_abs = MVCHy * y_sign; 
			int x_abs = MVCHx * x_sign;
			MVCHy = y_sign * (((y_abs >> 4) << 1) + roundtab[y_abs & 15]);
			MVCHx = x_sign * (((x_abs >> 4) << 1) + roundtab[x_abs & 15]);
		} else {
			MVCHx = macroblocks_info[macroblock_number][2];
			MVCHy = macroblocks_info[macroblock_number][6];
			if((MVCHx & 3) == 0) {
				MVCHx = MVCHx >> 1;
			} else {				
				MVCHx = (MVCHx >> 1) | 1;
			}
			if((MVCHy & 3) == 0) {
				MVCHy = MVCHy >> 1;
			} else {				
				MVCHy = (MVCHy >> 1) | 1;
			}
		}
		
		for(int block_number = 0; block_number < motion_vectors; block_number++) {
			MVx = macroblocks_info[macroblock_number][2 + block_number];
			MVy = macroblocks_info[macroblock_number][6 + block_number];
			  
			int x_index_block = (x_index_c << 1) + (block_number & 1);
			int y_index_block = (y_index_c << 1) + (block_number < 2 ? 0 : 1);

			if((block_number == 0) && (MVCHx == 0) && (MVCHy == 0)) {
				// process chrominance data
				int[] dst_data_cb = pixel_data[1];
				int[] dst_data_cr = pixel_data[2];
				int[] src_data_cb = src_pixel_data[1];
				int[] src_data_cr = src_pixel_data[2];
				int start_index = (y_index_block << 2) * buffer_width + (x_index_block << 2);
				for(int i = 0; i < 8; i++) {
					for(int data_index = start_index; data_index < (start_index + 8); data_index+=4) {
						dst_data_cr[data_index] += src_data_cr[data_index];
						dst_data_cb[data_index] += src_data_cb[data_index];
						dst_data_cr[data_index + 1] += src_data_cr[data_index + 1];
						dst_data_cb[data_index + 1] += src_data_cb[data_index + 1];
						dst_data_cr[data_index + 2] += src_data_cr[data_index + 2];
						dst_data_cb[data_index + 2] += src_data_cb[data_index + 2];
						dst_data_cr[data_index + 3] += src_data_cr[data_index + 3];
						dst_data_cb[data_index + 3] += src_data_cb[data_index + 3];
					}
					start_index += buffer_width;
				}
			}

			if((MVy == 0) && (MVx == 0)) {
				// apply zero motion vector
				// process lumminance data
				dst_data = pixel_data[0];
				src_data = src_pixel_data[0];
				int start_index = (y_index_block << 3) * buffer_width + (x_index_block << 3);
				for(int i = 0; i < motion_block_width; i++) {
					for(int data_index = start_index; data_index < (start_index + motion_block_width); data_index+=8) {
						dst_data[data_index] += src_data[data_index];
						dst_data[data_index + 1] += src_data[data_index + 1];
						dst_data[data_index + 2] += src_data[data_index + 2];
						dst_data[data_index + 3] += src_data[data_index + 3];
						dst_data[data_index + 4] += src_data[data_index + 4];
						dst_data[data_index + 5] += src_data[data_index + 5];
						dst_data[data_index + 6] += src_data[data_index + 6];
						dst_data[data_index + 7] += src_data[data_index + 7];
					}
					start_index += buffer_width;
				}
			}
			for(int n = 0; n < (block_number == 0 ? 3 : 1); n++) {
				int x_src_index = 0;
				int y_src_index = 0;
				int max_y = buffer_height;
				int max_x = buffer_width;
				if(n == 0) {				
					if((MVy == 0) && (MVx == 0)) {
						continue;
					}
					src_data = src_pixel_data[0];
					dst_data = pixel_data[0];
					half_x = (MVx & 1) == 1;
					half_y = (MVy & 1) == 1;
					block_width = motion_block_width;
					x_src_index = (x_index_block << 3) + (MVx >> 1);
					y_src_index = (y_index_block << 3) + (MVy >> 1); 
					dst_start_index = ((y_index_block * buffer_width) << 3) + (x_index_block << 3);
				} else {
					if((MVCHx == 0) && (MVCHy == 0)) {
						continue;
					}
					if(n == 1) {
						src_data = src_pixel_data[1];
						dst_data = pixel_data[1];
					} else {
						src_data = src_pixel_data[2];
						dst_data = pixel_data[2];
					}
					max_y >>= 1;
					max_x >>= 1;
					block_width = 8;
					half_x = (MVCHx & 1) == 1;
					half_y = (MVCHy & 1) == 1;
					x_src_index = (x_index_block << 2) + (MVCHx >> 1);
					y_src_index = (y_index_block << 2) + (MVCHy >> 1);
					dst_start_index = (y_index_block << 2) * buffer_width + (x_index_block << 2);
				}
				max_y --;
				max_x --;
				if((half_x == false) && (half_y == false)) {
					for(int i = 0; i < block_width; i++) {
						if(y_src_index >= max_y) {
							src_start_index = max_y * buffer_width;
						} else if(y_src_index >= 0) {
							src_start_index = y_src_index * buffer_width;
						} else {
							src_start_index = 0;
						}
						for(int j = 0, src_x = x_src_index; j < block_width; j++, src_x++) {
							if(src_x >= max_x) {
								dst_data[dst_start_index + j] += src_data[src_start_index + max_x];
							} else if(src_x >= 0) {
								dst_data[dst_start_index + j] += src_data[src_start_index + src_x];
							} else {
								dst_data[dst_start_index + j] += src_data[src_start_index];
							}
						}
						y_src_index++;
						dst_start_index += buffer_width;
					}
				} else if((half_x == true) && (half_y == false)) {
					for(int i = 0; i < block_width; i++) {
						if(y_src_index >= max_y) {
							src_start_index = max_y * buffer_width;
						} else if(y_src_index >= 0) {
							src_start_index = y_src_index * buffer_width;
						} else {
							src_start_index = 0;
						}
						for(int j = 0, src_x = x_src_index; j < block_width; j++, src_x++) {
							if(src_x >= max_x) {
								dst_data[dst_start_index + j] += src_data[src_start_index + max_x];
							} else if(src_x >= 0) {
								dst_data[dst_start_index + j] += (src_data[src_start_index + src_x] + src_data[src_start_index + src_x + 1] + 1 - rounding_control) >> 1;
							} else {
								dst_data[dst_start_index + j] += src_data[src_start_index];
							}
						}
						y_src_index++;
						dst_start_index += buffer_width;
					}
				} else if((half_x == false) && (half_y == true)) {
					for(int i = 0; i < block_width; i++, y_src_index++, dst_start_index += buffer_width) {
						if((y_src_index >= max_y) || (y_src_index < 0)) {
							if(y_src_index >= max_y) {
								src_start_index = max_y * buffer_width;
							} else {
								src_start_index = 0;
							}
							for(int j = 0, src_x = x_src_index; j < block_width; j++, src_x++) {
								if(src_x >= max_x) {
									dst_data[dst_start_index + j] += src_data[src_start_index + max_x];
								} else if(src_x >= 0) {
									dst_data[dst_start_index + j] += src_data[src_start_index + src_x];
								} else {
									dst_data[dst_start_index + j] += src_data[src_start_index];
								}
							}
							continue;
						}
						src_start_index = y_src_index * buffer_width;
						for(int j = 0, src_x = x_src_index; j < block_width; j++, src_x++) {
							if(src_x >= max_x) {
								dst_data[dst_start_index + j] += (src_data[src_start_index + max_x] + src_data[src_start_index + max_x + buffer_width] + 1 - rounding_control) >> 1;
							} else if(src_x >= 0) {
								dst_data[dst_start_index + j] += (src_data[src_start_index + src_x] + src_data[src_start_index + src_x + buffer_width] + 1 - rounding_control) >> 1;
							} else {
								dst_data[dst_start_index + j] += (src_data[src_start_index] + src_data[src_start_index + buffer_width] + 1 - rounding_control) >> 1;
							}
						}
					}
				} else if((half_x == true) && (half_y == true)) {
					for(int i = 0; i < block_width; i++, y_src_index++, dst_start_index += buffer_width) {
						if((y_src_index >= max_y) || (y_src_index < 0)) {
							if(y_src_index >= max_y) {
								src_start_index = max_y * buffer_width;
							} else {
								src_start_index = 0;
							}
							for(int j = 0, src_x = x_src_index; j < block_width; j++, src_x++) {
								if(src_x >= max_x) {
									dst_data[dst_start_index + j] += src_data[src_start_index + max_x];
								} else if(src_x >= 0) {
									dst_data[dst_start_index + j] += (src_data[src_start_index + src_x] + src_data[src_start_index + src_x + 1] + 1 - rounding_control) >> 1;
								} else {
									dst_data[dst_start_index + j] += src_data[src_start_index];
								}
							}
							continue;
						}
						src_start_index = y_src_index * buffer_width;
						for(int j = 0, src_x = x_src_index; j < block_width; j++, src_x++) {
							if(src_x >= max_x) {
								dst_data[dst_start_index + j] += (src_data[src_start_index + max_x] + src_data[src_start_index + max_x + buffer_width] + 1 - rounding_control) >> 1;
							} else if(src_x >= 0) {
								dst_data[dst_start_index + j] += (src_data[src_start_index + src_x] + src_data[src_start_index + src_x + 1] + src_data[src_start_index + src_x + buffer_width] + src_data[src_start_index + src_x + buffer_width + 1] + 2 - rounding_control) >> 2;
							} else {
								dst_data[dst_start_index + j] += (src_data[src_start_index] + src_data[src_start_index + buffer_width] + 1 - rounding_control) >> 1;
							}
						}
					}
				}
			}
		}
	}

	public int[][] getBlock(int macroblock_number, int n) {
		int[][][] input_blocks = null;
		int index = macroblock_number;
		if(n < 4) {
			int x_index = (macroblock_number % chrominace_blocks_per_line) * 2 + (n & 1);
			int y_index = (macroblock_number / chrominace_blocks_per_line) * 2 + (n < 2 ? 0 : 1);
			index = y_index * lumminance_blocks_per_line + x_index;
			input_blocks = lumminance_blocks;
		} else if(n == 4) {
			index = macroblock_number;
			input_blocks = cb_chrominace_blocks;
		} else {
			index = macroblock_number;
			input_blocks = cr_chrominace_blocks;
		}

		if((index < 0) || (index >= input_blocks.length)) {
			return null;
		}
		return input_blocks[index];
	}

	public int[][] getPreviousBlock() {
		return previousBlock;
	}

	public int getPreviousQuantiserScale() {
		return previousQuantiserScale;
	}
	
	public void setMacroblockInfo(int macroblock_number, int type, int quantiser_scale) {
		macroblocks_info[macroblock_number][0] = type;
		macroblocks_info[macroblock_number][1] = quantiser_scale;
		if(type >= 3) {
			// reset motion vector data
			macroblocks_info[macroblock_number][2] = 
				macroblocks_info[macroblock_number][3] = 
				macroblocks_info[macroblock_number][4] = 
				macroblocks_info[macroblock_number][5] = 0;
			macroblocks_info[macroblock_number][6] = 
				macroblocks_info[macroblock_number][7] = 
				macroblocks_info[macroblock_number][8] = 
				macroblocks_info[macroblock_number][9] = 0;
		}
	}
	
	public boolean getPredictionDirection(int macroblock_number, int n) {
		int[][] blockA = zero_block;
		int[][] blockB = zero_block;
		int[][] blockC = zero_block;
		int[][][] input_blocks = null;
		int x_index = macroblock_number % chrominace_blocks_per_line;
		int y_index = macroblock_number / chrominace_blocks_per_line;
		int blocks_per_line = 0;
		int quantiser_scale_c = 0;
		int quantiser_scale_a = 0;
		int prev_macroblock_number;
		int scale_factor = 0;
		if(n < 4) {
			x_index = x_index * 2 + (n & 1);
			y_index = y_index * 2 + (n < 2 ? 0 : 1);
			input_blocks = lumminance_blocks;
			blocks_per_line = lumminance_blocks_per_line;
			scale_factor = 1;
		} else if(n == 4) {
			input_blocks = cb_chrominace_blocks;
			blocks_per_line = chrominace_blocks_per_line;
		} else {
			input_blocks = cr_chrominace_blocks;
			blocks_per_line = chrominace_blocks_per_line;
		}
					
		int x_index_AB = x_index - 1;
		int y_index_BC = y_index - 1;
		if(x_index_AB >= 0) {
			prev_macroblock_number = (y_index >> scale_factor) * chrominace_blocks_per_line + (x_index_AB >> scale_factor);
			if(macroblocks_info[prev_macroblock_number][0] > 2) {
				blockA = input_blocks[y_index * blocks_per_line + x_index_AB];
				quantiser_scale_a = macroblocks_info[prev_macroblock_number][1];
			}
		}
		if(y_index_BC >= 0) {
			prev_macroblock_number = (y_index_BC >> scale_factor) * chrominace_blocks_per_line + (x_index >> scale_factor);
			if(macroblocks_info[prev_macroblock_number][0] > 2) {
				blockC = input_blocks[y_index_BC * blocks_per_line + x_index];
				quantiser_scale_c = macroblocks_info[prev_macroblock_number][1];
			}
		}
		if((y_index_BC >= 0) && (x_index_AB >= 0)) {
			prev_macroblock_number = (y_index_BC >> scale_factor) * chrominace_blocks_per_line + (x_index_AB >> scale_factor);
			if(macroblocks_info[prev_macroblock_number][0] > 2) {
				blockB = input_blocks[y_index_BC * blocks_per_line + x_index_AB];
			}
		}
		if(((Math.abs(blockA[0][0] - blockB[0][0]))) < (Math.abs(blockB[0][0] - blockC[0][0]))) {
			// predict from block C
			previousBlock = blockC;
			previousQuantiserScale = quantiser_scale_c;
			return true;
		} else {
			// predict from block A
			previousBlock = blockA;
			previousQuantiserScale = quantiser_scale_a;
			return false;
		}
	}
	
	public void clearMacroblockInfo(int current_macroblock_num) {
		if(current_macroblock_num > macroblocks_info.length) {
			current_macroblock_num = macroblocks_info.length;
		}
		for(int i = 0; i < current_macroblock_num; i++) {
			macroblocks_info[i][0] = macroblocks_info[i][1] = 0;
			for(int j = 2; j < 10; j++) {
				macroblocks_info[i][j] = Integer.MAX_VALUE;
			}
		}
	}
	
	public void clearFrame() {
		int pixel_data_length = pixel_data[0].length; 
		for(int i = 0; i < macroblocks_info.length; i++) {
			for(int j = 0; j < 10; j++) {
				macroblocks_info[i][j] = 0;
			}
		}
		for(int i = 0; i < pixel_data_length; i++) {
			pixel_data[0][i] = 0;
			pixel_data[1][i] = 128;
			pixel_data[2][i] = 128;
		}
	}

	public void transformBlock(int macroblock_number, int n) {
		int[][][] input_blocks = null;
		int index = macroblock_number;
		int[] output_data = null; 
		int x_index = macroblock_number % chrominace_blocks_per_line;
		int y_index = macroblock_number / chrominace_blocks_per_line;
		if(n < 4) {
			x_index = (x_index << 1) + (n & 1);
			y_index = (y_index << 1) + (n < 2 ? 0 : 1);
			index = y_index * lumminance_blocks_per_line + x_index;
			input_blocks = lumminance_blocks;
			output_data = pixel_data[0];
		} else if(n == 4) {
			input_blocks = cb_chrominace_blocks;
			output_data = pixel_data[1];
		} else {
			input_blocks = cr_chrominace_blocks;
			output_data = pixel_data[2];
		}
		if((index < 0) || (index >= input_blocks.length)) {
			return;
		}
		idct.idct(input_blocks[index], output_data, ((y_index * buffer_width) << 3) + (x_index << 3));
	}

	public int getFrameWidth() {
		return frame_width;
	}

	public int getFrameHeight() {
		return frame_height;
	}

	public int getBufferWidth() {
		return buffer_width;
	}

	public int getBufferHeight() {
		return buffer_height;
	}

	public int[][] getPixelData() {
		return pixel_data;
	}

	public int[][] getMacroblocks_info() {
		return macroblocks_info;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setRounding_control(byte rounding_control) {
		this.rounding_control = rounding_control;
	}

	public void setPlaying_time(long playing_time) {
		this.playing_time = playing_time;
	}

	public long getPlaying_time() {
		return playing_time;
	}

	public static int[] getRoundtab() {
		return roundtab;
	}

	public int getBuffer_height() {
		return buffer_height;
	}

	public void setBuffer_height(int buffer_height) {
		this.buffer_height = buffer_height;
	}

	public int getBuffer_width() {
		return buffer_width;
	}

	public void setBuffer_width(int buffer_width) {
		this.buffer_width = buffer_width;
	}

	public int[][][] getCb_chrominace_blocks() {
		return cb_chrominace_blocks;
	}

	public void setCb_chrominace_blocks(int[][][] cb_chrominace_blocks) {
		this.cb_chrominace_blocks = cb_chrominace_blocks;
	}

	public int getChrominace_blocks_per_line() {
		return chrominace_blocks_per_line;
	}

	public void setChrominace_blocks_per_line(int chrominace_blocks_per_line) {
		this.chrominace_blocks_per_line = chrominace_blocks_per_line;
	}

	public int[][][] getCr_chrominace_blocks() {
		return cr_chrominace_blocks;
	}

	public void setCr_chrominace_blocks(int[][][] cr_chrominace_blocks) {
		this.cr_chrominace_blocks = cr_chrominace_blocks;
	}

	public int getFrame_height() {
		return frame_height;
	}

	public void setFrame_height(int frame_height) {
		this.frame_height = frame_height;
	}

	public int getFrame_width() {
		return frame_width;
	}

	public void setFrame_width(int frame_width) {
		this.frame_width = frame_width;
	}

	public IDCT getIdct() {
		return idct;
	}

	public void setIdct(IDCT idct) {
		this.idct = idct;
	}

	public int[][][] getLumminance_blocks() {
		return lumminance_blocks;
	}

	public void setLumminance_blocks(int[][][] lumminance_blocks) {
		this.lumminance_blocks = lumminance_blocks;
	}

	public int getLumminance_blocks_per_line() {
		return lumminance_blocks_per_line;
	}

	public void setLumminance_blocks_per_line(int lumminance_blocks_per_line) {
		this.lumminance_blocks_per_line = lumminance_blocks_per_line;
	}

	public int getMVDx() {
		return MVDx;
	}

	public void setMVDx(int dx) {
		MVDx = dx;
	}

	public int getMVDy() {
		return MVDy;
	}

	public void setMVDy(int dy) {
		MVDy = dy;
	}

	public int getMVx() {
		return MVx;
	}

	public void setMVx(int vx) {
		MVx = vx;
	}

	public int getMVy() {
		return MVy;
	}

	public void setMVy(int vy) {
		MVy = vy;
	}

	public int[][] getPixel_data() {
		return pixel_data;
	}

	public void setPixel_data(int[][] pixel_data) {
		this.pixel_data = pixel_data;
	}

	public int[][][] getPredictor_indexes() {
		return predictor_indexes;
	}

	public void setPredictor_indexes(int[][][] predictor_indexes) {
		this.predictor_indexes = predictor_indexes;
	}

	public int getPx() {
		return Px;
	}

	public void setPx(int px) {
		Px = px;
	}

	public int getPy() {
		return Py;
	}

	public void setPy(int py) {
		Py = py;
	}

	public boolean isQuarter_sample() {
		return quarter_sample;
	}

	public void setQuarter_sample(boolean quarter_sample) {
		this.quarter_sample = quarter_sample;
	}

	public int[][] getZero_block() {
		return zero_block;
	}

	public void setZero_block(int[][] zero_block) {
		this.zero_block = zero_block;
	}

	public int[][] getZero_block2() {
		return zero_block2;
	}

	public void setZero_block2(int[][] zero_block2) {
		this.zero_block2 = zero_block2;
	}

	public byte getRounding_control() {
		return rounding_control;
	}

	public void setMacroblocks_info(int[][] macroblocks_info) {
		this.macroblocks_info = macroblocks_info;
	}

	public void setPreviousBlock(int[][] previousBlock) {
		this.previousBlock = previousBlock;
	}

	public void setPreviousQuantiserScale(int previousQuantiserScale) {
		this.previousQuantiserScale = previousQuantiserScale;
	}

}
