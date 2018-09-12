package org.kurento.tutorial.groupcall.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class BehappyController {
//	@RequestMapping( path = "/echo/{request}", method = RequestMethod.GET)
//	public ResponseEntity<?> echo(@PathVariable("request") String request ){
//		return ResponseEntity
//				.status(HttpStatus.SC_CONFLICT)
//				.body(new User("1001", request));
//	}
	
	@RequestMapping( path = "/upload", method = RequestMethod.POST)
	public ResponseEntity<?> uploadAudio(HttpServletRequest req){
		InputStream fileIn = null;
		try {
			Part audio = req.getPart("fdata");
			fileIn = audio.getInputStream();
			FileUtils.copyInputStreamToFile(fileIn, new File(String.format("C:/Temp/bh/audio-%s.mp3", System.currentTimeMillis())));
		} catch (IOException | ServletException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(fileIn);
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", 0);
		return ResponseEntity.ok(result);
	}
}
