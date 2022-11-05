package com.grain.teacher.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.grain.teacher.entity.EduChapter;
import com.grain.teacher.entity.EduVideo;
import com.grain.teacher.entity.vo.OneChapter;
import com.grain.teacher.entity.vo.TwoVideo;
import com.grain.common.exception.EduException;
import com.grain.teacher.mapper.EduChapterMapper;
import com.grain.teacher.service.EduChapterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.grain.teacher.service.EduVideoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author Dragon Wen
 * @since 2020-03-03
 */
@Service
public class EduChapterServiceImpl extends ServiceImpl<EduChapterMapper, EduChapter> implements EduChapterService {

    @Autowired
    private EduVideoService videoService;

    @Override
    public boolean saveChapter(EduChapter chapter) {
        if(chapter == null){
            return false;
        }
        QueryWrapper<EduChapter> chapterWrapper = new QueryWrapper<>();
        chapterWrapper.eq("title", chapter.getTitle());
        Integer count = baseMapper.selectCount(chapterWrapper);
        if(count > 0){
            return false;
        }
        int insert = baseMapper.insert(chapter);
        return insert == 1;
    }

    @Override
    public List<OneChapter> queryChapterAndVideoList(String id) {
        //定义一个章节集合
        List<OneChapter> oneChapterList = new ArrayList<>();
        QueryWrapper<EduChapter> chapterWrapper = new QueryWrapper<>();
        chapterWrapper.eq("course_id",id);
        chapterWrapper.orderByAsc("sort", "id");
        //先查询章节列表集合
        List<EduChapter> chapterList = baseMapper.selectList(chapterWrapper);
        //再遍历章节集合，获取每个章节ID
        for (EduChapter eduChapter : chapterList) {
            OneChapter oneChapter = new OneChapter();
            BeanUtils.copyProperties(eduChapter,oneChapter);
            //再根据每个章节的ID查询节点的列表
            QueryWrapper<EduVideo> videoWrapper = new QueryWrapper<>();
            videoWrapper.eq("chapter_id",oneChapter.getId());
            videoWrapper.orderByAsc("sort", "id");
            List<EduVideo> eduVideoList = videoService.list(videoWrapper);
            //把小节的列表添加章节中
            for(EduVideo eduVideo : eduVideoList){
                TwoVideo twoVideo = new TwoVideo();
                BeanUtils.copyProperties(eduVideo,twoVideo);
                oneChapter.getChildren().add(twoVideo);
            }
            oneChapterList.add(oneChapter);
        }

        return oneChapterList;
    }

    @Override
    public boolean updateChapterById(EduChapter chapter) {
        if(chapter == null){
            return false;
        }
        QueryWrapper<EduChapter> chapterWrapper = new QueryWrapper<>();
        chapterWrapper.eq("title", chapter.getTitle());
        Integer count = baseMapper.selectCount(chapterWrapper);
        if(count > 0){
            return false;
        }
        int update = baseMapper.updateById(chapter);
        return update == 1;
    }

    @Override
    public boolean deleteChapterById(String id) {
        //判断是否存在小节
        QueryWrapper<EduVideo> wrapper = new QueryWrapper<>();
        wrapper.eq("chapter_id",id);
        List<EduVideo> list = videoService.list(wrapper);
        if(list.size() != 0){
            throw new EduException(20001,"该分章节下存在视频课程，请先删除视频课程");
        }
        //删除章节
        int delete = baseMapper.deleteById(id);
        return delete > 0;

    }

    @Override
    public void deleteChapterByCourseId(String courseId) {
        QueryWrapper<EduChapter> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId);
        List<EduChapter> chapterList = baseMapper.selectList(wrapper);

        // 定义一个集合存放章节ID
        List<String> chapterIds = new ArrayList<>();
        // 获取课程ID
        for (EduChapter chapter : chapterList) {
            if(!StringUtils.isEmpty(chapter.getId())){
                chapterIds.add(chapter.getId());
            }
        }

        if(chapterIds.size() > 0){
            baseMapper.deleteBatchIds(chapterIds);
        }
    }
}
