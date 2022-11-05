package com.grain.teacher.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.grain.common.result.Result;
import com.grain.teacher.entity.EduCourse;
import com.grain.teacher.entity.query.CourseQuery;
import com.grain.teacher.entity.vo.CourseVo;
import com.grain.teacher.service.EduCourseDescriptionService;
import com.grain.teacher.service.EduCourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author Dragon Wen
 * @since 2020-03-03
 */
@Api(value = "课程基本信息管理")
@RestController
@RequestMapping("/course")
@CrossOrigin
public class EduCourseController {

    @Autowired
    private EduCourseService courseService;

    @Autowired
    private EduCourseDescriptionService courseDescriptionService;

    /**
     * 保存基本信息
     */
    @ApiOperation(value = "保存基本信息")
    @PostMapping("save")
    public Result save(@RequestBody CourseVo courseVo){

        String courseId = courseService.saveVo(courseVo);
        return Result.ok().data("id", courseId);
    }

    /**
     * 根据课程ID获取课程基本信息和描述
     */
    @ApiOperation(value = "根据课程ID获取课程基本信息和描述")
    @GetMapping("{id}")
    public Result getCourseVoById(@PathVariable String id){
        CourseVo vo = courseService.getCourseVoById(id);
        return Result.ok().data("courseInfo",vo);
    }

    /**
     * 修改课程基本信息和描述
     */
    @ApiOperation(value = "修改课程基本信息和描述")
    @PutMapping("update")
    public Result updateCourseVo(@RequestBody CourseVo courseVo){
        boolean flag = courseService.updateCourseVo(courseVo);
        if(flag){
            return Result.ok();
        } else {
            return Result.error();
        }
    }

    /**
     * 根据条件分页查询课程列表
     * @param page
     * @param limit
     * @param courseQuery
     * @return
     */
    @ApiOperation(value = "分页课程列表")
    @PostMapping("{page}/{limit}")
    public Result getPageList(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Integer page,
            @ApiParam(name = "limit" ,value = "每页记录数", required = true)
            @PathVariable Integer limit,
            @ApiParam(name = "courseQuery", value = "查询对象", required = false)
            @RequestBody CourseQuery courseQuery){

        Page<EduCourse> pageParam = new Page<>(page, limit);
        courseService.getPageList(pageParam, courseQuery);
        List<EduCourse> records = pageParam.getRecords();
        long total = pageParam.getTotal();
        return Result.ok().data("total", total).data("rows",records);

    }

    /**
     *  根据课程ID删除课程信息
     * @param id
     * @return
     */
    @ApiOperation(value = "根据ID删除课程")
    @DeleteMapping("{id}")
    public Result removeById(
            @ApiParam(name = "id", value = "课程ID", required = true)
            @PathVariable String id){

        boolean result = courseService.deleteCourseById(id);
        if(result){
            return Result.ok();
        }else{
            return Result.error().message("删除失败");
        }
    }

    /**
     * 根据课程Id查询课程Map对象
     * @param id
     * @return
     */
    @ApiOperation(value = "")
    @GetMapping("vo/{id}")
    public Result getCoursePublishById(@PathVariable String id){
        Map<String, Object> map = courseService.getMapById(id);
        return Result.ok().data(map);
    }

    /**
     * 根据课程Id修改课程状态
     * @param id
     * @return
     */
    @ApiOperation(value = "")
    @PutMapping("/updateStatus/{id}")
    public Result updateByStatusById(@PathVariable String id){
        Boolean flag = courseService.updateStatusById(id);
        if(flag){
            return Result.ok();
        } else{
            return Result.error();
        }
    }

}

