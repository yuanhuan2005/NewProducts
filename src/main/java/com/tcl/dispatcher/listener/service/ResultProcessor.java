package com.tcl.dispatcher.listener.service;

import com.tcl.dispatcher.listener.domain.ProcessResult;

public interface ResultProcessor
{
	public void process(ProcessResult result);
}
