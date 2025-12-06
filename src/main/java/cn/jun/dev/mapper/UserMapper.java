package cn.jun.dev.mapper;

import cn.jun.dev.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

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
        @Insert("INSERT INTO user(username, password, email, nickname, avatar, status, role, created_at, updated_at) " +
                        "VALUES(#{username}, #{password}, #{email}, #{nickname}, #{avatar}, #{status}, #{role}, NOW(), NOW())")
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

        /**
         * 分页查询所有用户（管理员功能）
         */
        @Select("SELECT * FROM user ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}")
        List<User> findAllWithPage(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

        /**
         * 统计用户总数
         */
        @Select("SELECT COUNT(*) FROM user")
        Long countAll();

        /**
         * 更新用户状态
         */
        @Update("UPDATE user SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
        int updateStatus(@Param("id") Long id, @Param("status") Integer status);

        /**
         * 更新用户头像
         */
        @Update("UPDATE user SET avatar = #{avatar}, updated_at = NOW() WHERE id = #{id}")
        int updateAvatar(@Param("id") Long id, @Param("avatar") String avatar);
}
