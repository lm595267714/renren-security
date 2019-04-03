/**
 * Copyright 2018 人人开源 http://www.renren.io
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.renren.modules.oss.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import io.renren.common.utils.PageUtils;
import io.renren.datasources.annotation.DataSource;
import io.renren.modules.oss.dao.AssayDao;
import io.renren.modules.oss.entity.AssayDetailEntity;
import io.renren.modules.oss.entity.AssayEntity;
import io.renren.modules.oss.entity.AssayQueryDto;
import io.renren.modules.oss.entity.SysOssEntity;
import io.renren.modules.oss.service.AssayService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("assayService")
public class AssayServiceImpl extends ServiceImpl<AssayDao, AssayEntity> implements AssayService {

    @Autowired
    private AssayDao assayDao;

    @Override
    @DataSource(name = "second")
    public PageUtils queryPage(Map<String, Object> params, AssayQueryDto assayParam) {

        Integer page = 1;
        Integer limit = 10;
        if (StringUtils.isNotEmpty(params.get("page").toString())) {
            page = Integer.valueOf(params.get("page").toString());
        }
        if (StringUtils.isNotEmpty(params.get("limit").toString())) {
            limit = Integer.valueOf(params.get("limit").toString());
        }
        Integer offset = (page - 1) * limit;

        assayParam.setOffset(offset);
        assayParam.setLimit(limit);
        List<AssayEntity> dataList = assayDao.queryPage(assayParam);
        Integer totalSize = assayDao.queryTotal(assayParam);
        return new PageUtils(dataList, totalSize, limit, page);
    }

    @Override
    public PageUtils queryDetail(Map<String, Object> params) {
        List<AssayDetailEntity> assayList = Lists.newArrayList();
        PageUtils page = new PageUtils(assayList, 200, 10, 1);
        for (int i = 0; i < 12; i++) {
            AssayDetailEntity entity = new AssayDetailEntity();
            entity.setItemName("点青霉/烟曲霉/分枝孢菌/交链孢菌组合");
            entity.setItemResult("阴性");
            entity.setTips("");
            entity.setReference("阴性");
            entity.setShortName("DQM");
            assayList.add(entity);
        }

        return page;
    }

}
