/**
 * Copyright 2008 - 2009
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
package org.ripple.power.sound;

import java.io.InterruptedIOException;

import mediaframe.mpeg4.video.VideoFrame;

public interface IMpeg4 {

	public abstract void playerend();

	public abstract int getVideoLength();

	public abstract void nextFrame(VideoFrame videoFrame)
			throws InterruptedIOException;

	public abstract void startReBuffering();

	public abstract void stopReBuffering() throws InterruptedIOException;

	public abstract void stopBuffering() throws InterruptedIOException;

}
