package com.hkgov.csb.eproof.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cretInfo")
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class CretInfoController{
}
