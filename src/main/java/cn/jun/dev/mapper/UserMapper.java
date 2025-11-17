package cn.jun.dev.mapper;

import cn.jun.dev.entity.User;
import org.apache.ibatis.annotations.*;

/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper {
    
    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);
    
    /**
     * 根据邮箱查询用户
     */
    @Select("SELECT * FROM user WHERE email = #{email}")
    User findByEmail(String email);
    
    /**
     * 根据ID查询用户
     */
    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);
    
    /**
     * 插入用户
     */
    @Insert("INSERT INTO user(username, password, email, nickname, avatar, status, created_at, updated_at) " +
            "VALUES(#{username}, #{password}, #{email}, #{nickname}, #{avatar}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    /**
     * 更新用户信息
     */
    @Update("UPDATE user SET nickname = #{nickname}, avatar = #{avatar}, gender = #{gender}, " +
            "birthday = #{birthday}, updated_at = NOW() WHERE id = #{id}")
    int update(User user);
    
    /**
     * 更新密码
     */
    @Update("UPDATE user SET password = #{password}, updated_at = NOW() WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);
}




