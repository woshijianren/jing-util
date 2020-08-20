package com.util;

import lombok.Data;

import java.io.Serializable;

/**
 * @author daven
 * @date 2020/5/21
 */
@Data
public class BaseReturn implements Serializable {
    private int returnCode = 0;
    private String returnMessage = "";
}
