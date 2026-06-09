---
doc_type: learning
track: pitfall
title: Mockito 中 @InjectMocks 依赖漏声明 Mock 属性导致 NPE
created: 2026-06-09
tags: [mockito, unit-test, java, npe]
component: backend-test
---

# Mockito 中 @InjectMocks 依赖漏声明 Mock 属性导致 NPE

## 1. 观察到的现象
在新开发的功能中增加了对某些级联依赖（例如调用 `UserRoleMapper`）进行校验的逻辑。本地编译通过，但是在执行 Maven 单元测试 `mvn test` 时，原有的 `testDeleteUser_Success` 和 `testUpdateUserStatus_Success` 单元测试在没有做任何修改的情况下直接抛出 `NullPointerException`（NPE）：
```
Cannot invoke "com.demo.login.mapper.UserRoleMapper.selectList(...)" because "this.userRoleMapper" is null
```

## 2. 尝试过的解法
- **解法尝试**：直接重试或重新配置 Maven。发现 NPE 是必然出现的。
- **真正根因**：检查 `UserManagementServiceTest.java` 发现，测试类中只声明了 `@Mock private UserMapper userMapper;`，但没有声明 `UserRoleMapper` 等其他 Mapper。
  Mockito 的 `@InjectMocks` 注解在向 `UserManagementServiceImpl` 注入 Mock 对象时，仅会去匹配测试类中声明了 `@Mock` 的那些字段。对于在测试类中未声明的字段，Mockito 会直接将其留为 `null` 注入。
  在以前的代码版本中，单测方法所覆盖的逻辑路径从未触发过对其他依赖的调用，所以一直没有暴露。一旦新代码让某些单测用例走到了会调用其他依赖的分支路径，就会发生 null 指针引用崩溃。

## 3. 防范与最佳实践
- **完整对齐依赖**：当一个类使用 `@InjectMocks` 进行测试时，它的测试类中应当完整对齐该 Service 里通过 `@Autowired` 声明的**所有**依赖 Mapper / Service。对于那些在特定用例中不需要进行特定 stub 行为的依赖，也应该声明为 `@Mock` 属性，以便 Mockito 自动注入默认的 Mock 对象（默认会返回空集合或空对象，而不会是 `null`）。
- **新增依赖同步更新测试**：往后在 Service 实体中新增 `@Autowired` 依赖成员时，必须**第一反应**去其对应的单元测试类中增加对应的 `@Mock` 依赖声明，以防止原有测试用例因为缺少依赖 Mock 而突发 NPE 故障。
