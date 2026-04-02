from django.db import models

class User(models.Model):
    id = models.AutoField(primary_key=True)
    username = models.CharField(max_length=50, unique=True)
    password = models.CharField(max_length=255)
    nickname = models.CharField(max_length=50, null=True, blank=True)
    avatar = models.CharField(max_length=255, null=True, blank=True)
    email = models.CharField(max_length=100, null=True, blank=True)
    phone = models.CharField(max_length=20, null=True, blank=True)
    status = models.IntegerField(default=1)
    deleted = models.IntegerField(default=0)
    create_time = models.DateTimeField(auto_now_add=True)
    update_time = models.DateTimeField(auto_now=True)
    create_by = models.CharField(max_length=50, null=True, blank=True)
    update_by = models.CharField(max_length=50, null=True, blank=True)
    remark = models.CharField(max_length=500, null=True, blank=True)

    class Meta:
        managed = False
        db_table = 'sys_user'

class Role(models.Model):
    id = models.AutoField(primary_key=True)
    role_name = models.CharField(max_length=50)
    role_key = models.CharField(max_length=50, unique=True)
    is_system = models.IntegerField(default=0)
    status = models.IntegerField(default=1)
    sort = models.IntegerField(default=0)
    remark = models.CharField(max_length=500, null=True, blank=True)
    deleted = models.IntegerField(default=0)
    create_time = models.DateTimeField(auto_now_add=True)
    update_time = models.DateTimeField(auto_now=True)
    create_by = models.CharField(max_length=50, null=True, blank=True)
    update_by = models.CharField(max_length=50, null=True, blank=True)

    class Meta:
        managed = False
        db_table = 'sys_role'

class UserRole(models.Model):
    id = models.AutoField(primary_key=True)
    user_id = models.IntegerField()
    role_id = models.IntegerField()
    create_time = models.DateTimeField(auto_now_add=True)

    class Meta:
        managed = False
        db_table = 'sys_user_role'

class Menu(models.Model):
    id = models.AutoField(primary_key=True)
    parent_id = models.IntegerField(default=0)
    menu_name = models.CharField(max_length=50)
    menu_type = models.CharField(max_length=1, default='C')
    path = models.CharField(max_length=200, null=True, blank=True)
    component = models.CharField(max_length=200, null=True, blank=True)
    perms = models.CharField(max_length=100, null=True, blank=True)
    icon = models.CharField(max_length=50, null=True, blank=True)
    sort = models.IntegerField(default=0)
    visible = models.IntegerField(default=1)
    status = models.IntegerField(default=1)
    deleted = models.IntegerField(default=0)
    create_time = models.DateTimeField(auto_now_add=True)
    update_time = models.DateTimeField(auto_now=True)
    create_by = models.CharField(max_length=50, null=True, blank=True)
    update_by = models.CharField(max_length=50, null=True, blank=True)
    remark = models.CharField(max_length=500, null=True, blank=True)

    class Meta:
        managed = False
        db_table = 'sys_menu'

class RoleMenu(models.Model):
    id = models.AutoField(primary_key=True)
    role_id = models.IntegerField()
    menu_id = models.IntegerField()
    create_time = models.DateTimeField(auto_now_add=True)

    class Meta:
        managed = False
        db_table = 'sys_role_menu'

class Position(models.Model):
    id = models.AutoField(primary_key=True)
    position_name = models.CharField(max_length=50)
    position_code = models.CharField(max_length=50, unique=True)
    status = models.IntegerField(default=1)
    sort = models.IntegerField(default=0)
    remark = models.CharField(max_length=500, null=True, blank=True)
    deleted = models.IntegerField(default=0)
    create_time = models.DateTimeField(auto_now_add=True)
    update_time = models.DateTimeField(auto_now=True)
    create_by = models.CharField(max_length=50, null=True, blank=True)
    update_by = models.CharField(max_length=50, null=True, blank=True)

    class Meta:
        managed = False
        db_table = 'sys_position'

class UserPosition(models.Model):
    id = models.AutoField(primary_key=True)
    user_id = models.IntegerField()
    position_id = models.IntegerField()
    create_time = models.DateTimeField(auto_now_add=True)

    class Meta:
        managed = False
        db_table = 'sys_user_position'
