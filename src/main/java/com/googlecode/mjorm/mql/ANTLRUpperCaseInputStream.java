package com.googlecode.mjorm.mql;

import java.io.IOException;
import java.io.InputStream;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CharStream;

public class ANTLRUpperCaseInputStream
	extends ANTLRInputStream {

	public ANTLRUpperCaseInputStream(InputStream ips)
		throws IOException {
		super(ips);
	}

	@Override
	public int LA(int i) {
		int returnChar = super.LA(i);
		if (returnChar==CharStream.EOF) {
			return returnChar;
		} else if (returnChar==0) {
			return returnChar;
		}
        return Character.toUpperCase((char)returnChar);
	}

}
