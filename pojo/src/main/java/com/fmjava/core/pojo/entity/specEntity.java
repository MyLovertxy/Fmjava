package com.fmjava.core.pojo.entity;


import com.fmjava.core.pojo.specification.Specification;
import com.fmjava.core.pojo.specification.SpecificationOption;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class specEntity implements Serializable {
    private Specification specification;//规格对象
    private List<SpecificationOption> specOptionList;//规格选项的集合
}
