﻿# 鐢ㄦ埛涓庢潈闄愮鐞嗘ā鍧?API 鏂囨。

## 1. 鐩爣涓庤寖鍥?
鏈帴鍙ｆ枃妗ｇ敤浜庢敮鎸佸墠绔€滅敤鎴蜂笌鏉冮檺绠＄悊妯″潡鈥濊仈璋冿紝瑕嗙洊涓夌被瑙掕壊锛?
- 瀛︾敓锛氭祻瑙堢ぞ鍥€佸姞鍏ョぞ鍥€佹姤鍚嶆椿鍔?
- 绀惧洟绠＄悊鍛橈細缁存姢鏈ぞ鍥俊鎭€佹垚鍛樸€佺粡璐?
- 瀛︽牎绠＄悊鍛橈細瀹℃壒绀惧洟浜嬮」銆佺鐞嗙敤鎴风姸鎬併€佺粺璁＄洃绠?

鏈枃妗ｆ帴鍙ｅ凡涓庡墠绔唬鐮佸搴旓細
- `src/api/auth.js`
- `src/api/user-permission.js`
- `src/views/dashboard/student/StudentDashboardView.vue`
- `src/views/dashboard/club-admin/ClubAdminDashboardView.vue`
- `src/views/dashboard/school-admin/SchoolAdminDashboardView.vue`

## 2. 閴存潈涓庡畨鍏ㄧ害瀹?
- 鍗忚锛歚HTTPS`
- Token锛歚Authorization: Bearer <accessToken>`
- 缁熶竴韬唤璁よ瘉锛氭敮鎸佹牎鍥?SSO 绁ㄦ嵁鎹㈠彇绯荤粺 Token
- 鏉冮檺妯″瀷锛歚RBAC`锛堣鑹诧級 + `Scope`锛堟暟鎹寖鍥达紝渚嬪鈥滀粎鏈ぞ鍥⑩€濓級
- 鐢ㄦ埛鐘舵€佹帶鍒讹細`ACTIVE/FROZEN/GRADUATED/CANCELED`
- 鎵€鏈夊鏍＄鐞嗗憳鍏抽敭鎿嶄綔闇€璁板綍鎿嶄綔鐣欑棔锛屼絾**闈炲鎵圭被鍔熻兘绂佹鍐欏叆 `audit_log`**锛堥伒寰?`docs/database-design.md` 绗?鑺傦級

## 3. 缁熶竴杩斿洖鏍煎紡
```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

- `code = 0`锛氭垚鍔?
- `code != 0`锛氫笟鍔″け璐?

甯歌閿欒鐮佸缓璁細
- `1001` 鍙傛暟閿欒
- `1002` 鏈櫥褰曟垨 token 澶辨晥
- `1003` 鏃犳潈闄?
- `1004` 璐﹀彿鐘舵€佷笉鍙敤锛堝喕缁?姣曚笟/娉ㄩ攢锛?
- `1005` 璧勬簮涓嶅瓨鍦?
- `1006` 鏁版嵁鑼冨洿瓒婃潈锛堜緥濡傜ぞ鍥㈢鐞嗗憳鎿嶄綔鍏朵粬绀惧洟锛?
- `1099` 绯荤粺寮傚父

## 4. 瑙掕壊涓庢潈闄愮爜

### 4.1 瑙掕壊鐮?
- `STUDENT`
- `CLUB_ADMIN`
- `SCHOOL_ADMIN`

### 4.2 鏉冮檺鐮?
- `student:club:browse`
- `student:club:join`
- `student:event:signup`
- `club:info:manage`
- `club:event:manage`
- `club:finance:manage`
- `school:club:approval`
- `school:finance:audit`
- `school:data:stats`
- `school:user-status:manage`

## 5. 璁よ瘉涓庝細璇濇帴鍙?

### 5.1 璐﹀彿瀵嗙爜鐧诲綍
- `POST /api/auth/login`

Request:
```json
{
  "username": "20230001",
  "password": "123456"
}
```

Response `data`:
```json
{
  "token": "jwt-token",
  "refreshToken": "refresh-token",
  "userId": 1001,
  "username": "20230001",
  "realName": "寮犱笁",
  "permissionType": "STUDENT",
  "status": "ACTIVE",
  "clubId": null
}
```

### 5.2 鏍″洯缁熶竴韬唤璁よ瘉鐧诲綍锛圫SO锛?
- `POST /api/auth/sso/login`

Request:
```json
{
  "ticket": "sso-ticket-from-cas"
}
```

Response锛氬悓 `5.1`

### 5.3 鑾峰彇褰撳墠鐧诲綍鐢ㄦ埛淇℃伅
- `GET /api/auth/me`

Response `data`:
```json
{
  "userId": 1001,
  "username": "20230001",
  "realName": "寮犱笁",
  "permissionType": "STUDENT",
  "status": "ACTIVE",
  "clubId": null,
  "permissions": ["student:club:browse", "student:club:join", "student:event:signup"]
}
```

### 5.4 鍒锋柊 Token
- `POST /api/auth/refresh-token`

### 5.5 閫€鍑虹櫥褰?
- `POST /api/auth/logout`

## 6. 瀛︾敓绔帴鍙?

### 6.1 绀惧洟鍏紑鍒楄〃
- `GET /api/clubs/public?pageNum=1&pageSize=8&keyword=&category=`

Response `data`:
```json
{
  "records": [
    {
      "id": 1,
      "clubName": "闊充箰绀?,
      "category": "鏂囧寲绫?,
      "instructorName": "鏉庤€佸笀"
    }
  ],
  "total": 100
}
```

### 6.2 鐢宠鍔犲叆绀惧洟
- `POST /api/clubs/{clubId}/join`

### 6.3 鍙姤鍚嶆椿鍔ㄥ垪琛?
- `GET /api/events/open?pageNum=1&pageSize=8`

Response `data.records` 瀛楁寤鸿鍖呭惈锛?
- `id`
- `title`
- `location`
- `limitCount`
- `remainingSlots`

### 6.4 鎶ュ悕娲诲姩
- `POST /api/events/{eventId}/signup`

楂樺苟鍙戣姹傦細
- 鍚庣蹇呴』浣跨敤缂撳瓨鍘熷瓙鎵ｅ噺鎴栧垎甯冨紡閿侀伩鍏嶈秴鍗?
- 瓒呴杩斿洖鏄庣‘閿欒淇℃伅锛堝鈥滃悕棰濆凡婊♀€濓級

## 7. 绀惧洟绠＄悊鍛樻帴鍙ｏ紙浠呮湰绀惧洟锛?

### 7.1 获取我的社团信息
- `GET /api/club-admin/me/club`

Response `data` 建议包含：
```json
{
  "clubName": "音乐社",
  "category": "文化类",
  "instructorName": "李老师",
  "introduction": "面向全校同学开展音乐排练与交流活动。",
  "purpose": "丰富校园文化生活"
}
```

### 7.2 更新我的社团信息
- `PUT /api/club-admin/me/club`

Request:
```json
{
  "clubName": "音乐社",
  "category": "文化类",
  "instructorName": "李老师",
  "introduction": "面向全校同学开展音乐排练与交流活动。",
  "purpose": "丰富校园文化生活"
}
```

字段说明：
- `introduction`：社团简介，建议长度 `<= 300`

### 7.3 鑾峰彇鎴戠殑绀惧洟鎴愬憳
- `GET /api/club-admin/me/members?pageNum=1&pageSize=10`

### 7.4 璋冩暣鎴愬憳鑱屼綅
- `PATCH /api/club-admin/me/members/{memberId}/role`

Request:
```json
{
  "positionName": "閮ㄩ暱"
}
```

### 7.5 绉婚櫎鎴愬憳
- `DELETE /api/club-admin/me/members/{memberId}`

### 7.6 鑾峰彇鏈ぞ鍥㈢粡璐硅褰?
- `GET /api/club-admin/me/finances?pageNum=1&pageSize=10`

Response `records` 寤鸿鍖呭惈锛?
- `id`
- `type` 鎴?`typeName`
- `amount`
- `status`

## 8. 瀛︽牎绠＄悊鍛樻帴鍙?

### 8.1 用户列表
- `GET /api/school-admin/users?pageNum=1&pageSize=10&roleCode=&status=&keyword=`

Query 参数：
- `pageNum`：页码，默认 `1`
- `pageSize`：每页条数，默认 `10`
- `keyword`：关键词，按姓名或账号模糊匹配
- `roleCode`：角色筛选，可选值：
  - `STUDENT`
  - `CLUB_ADMIN`
  - `SCHOOL_ADMIN`
- `status`：状态筛选，可选值：
  - `ACTIVE`
  - `FROZEN`
  - `GRADUATED`
  - `CANCELED`

Response（`data`）示例：
```json
{
  "records": [
    {
      "id": 2001,
      "username": "20230001",
      "realName": "张三",
      "roleCode": "STUDENT",
      "status": "ACTIVE"
    }
  ],
  "total": 1,
  "pageNum": 1,
  "pageSize": 10
}
```

说明：
- 返回字段 `status` 由后端统一映射：
  - `u.graduate_at <= now` 优先返回 `GRADUATED`
  - 否则按 `u.status` 映射为 `ACTIVE/FROZEN/CANCELED`

### 8.2 璋冩暣鐢ㄦ埛鐘舵€?
- `PATCH /api/school-admin/users/{userId}/status`

Request锛堢鐢ㄨ处鍙凤級:
```json
{
  "status": "FROZEN",
  "reason": "杩濊鎿嶄綔"
}
```

Request锛堝彇娑堢鐢紝鎭㈠璐﹀彿锛?
```json
{
  "status": "ACTIVE",
  "reason": "瀛︽牎绠＄悊鍛樻墜鍔ㄥ彇娑堢鐢?
}
```

鍓嶇鎸夐挳绾﹀畾锛?
- 褰撳墠鐘舵€侀潪 `FROZEN`锛氭寜閽伆鑹诧紝鏂囨鈥滅鐢ㄨ处鍙封€濓紝鐐瑰嚮鍚庡彂閫?`status=FROZEN`銆?
- 褰撳墠鐘舵€佷负 `FROZEN`锛氭寜閽豢鑹诧紝鏂囨鈥滃彇娑堢鐢ㄨ处鍙封€濓紝鐐瑰嚮鍚庡彂閫?`status=ACTIVE`銆?

鏃ュ織闄愬埗锛?
- 鐢ㄦ埛鐘舵€佸垏鎹㈠睘浜庨潪瀹℃壒绫诲姛鑳斤紝**绂佹鍐欏叆 `audit_log`**锛堥伒寰?`docs/database-design.md` 绗?鑺傦級銆?

### 8.3 璋冩暣鐢ㄦ埛瑙掕壊
- `PATCH /api/school-admin/users/{userId}/role`

Request锛堣涓虹ぞ鍥㈢鐞嗗憳锛?
```json
{
  "roleCode": "CLUB_ADMIN"
}
```

Request锛堝彇娑堢ぞ鍥㈢鐞嗗憳锛屾仮澶嶅鐢燂級:
```json
{
  "roleCode": "STUDENT"
}
```

鍓嶇鎸夐挳绾﹀畾锛?
- 褰撳墠瑙掕壊闈?`CLUB_ADMIN`锛氭寜閽伆鑹诧紝鏂囨鈥滆涓虹ぞ鍥㈢鐞嗗憳鈥濓紝鐐瑰嚮鍚庡彂閫?`roleCode=CLUB_ADMIN`銆?
- 褰撳墠瑙掕壊涓?`CLUB_ADMIN`锛氭寜閽豢鑹诧紝鏂囨鈥滃彇娑堢ぞ鍥㈢鐞嗗憳鈥濓紝鐐瑰嚮鍚庡彂閫?`roleCode=STUDENT`銆?

### 8.4 社团管理列表
- `GET /api/school-admin/clubs/manage?pageNum=1&pageSize=10&keyword=&category=`

Response:
```json
{
  "records": [
    {
      "clubId": 283357707223040,
      "clubName": "晨曦音乐社",
      "category": "文化类",
      "status": 2,
      "applyStatus": 4,
      "initiatorRealName": "张三",
      "instructorName": "李老师",
      "canEdit": true,
      "canDelete": false,
      "editDisabledReason": "",
      "deleteDisabledReason": "仅驳回待审核或已注销社团允许删除"
    }
  ],
  "total": 1,
  "pageNum": 1,
  "pageSize": 10
}
```

状态定义：
- `status`（club.status）：
  - `1` 待审核
  - `2` 正常
  - `3` 限期整改/冻结
  - `4` 待注销
  - `5` 已注销
- `applyStatus`（club_apply.apply_status）：
  - `1` 待初审
  - `2` 答辩中
  - `3` 公示中
  - `4` 通过
  - `5` 驳回

前端按钮控制建议（后端返回为准）：
- `canEdit`: 是否允许修改
- `canDelete`: 是否允许删除
- `editDisabledReason/deleteDisabledReason`: 禁用原因文案

后端判定逻辑（简版，建议直接实现）：
- 修改允许（`canEdit=true`）：
  - `status != 5` 且 `applyStatus` 不在 `[1,2,3]`
- 修改禁止（`canEdit=false`）：
  - `status = 5`（已注销）
  - 或 `applyStatus` 在 `[1,2,3]`（审批进行中）
- 删除允许（`canDelete=true`）：
  - (`applyStatus = 5` 且 `status = 1`)
  - 或 `status = 5`
- 删除禁止（`canDelete=false`）：
  - 其他状态组合一律禁止

推荐伪代码：
```text
canEdit = (status != 5) && (applyStatus not in [1,2,3])
canDelete = ((applyStatus == 5 && status == 1) || status == 5)
```

说明：
- 前端只消费 `canEdit/canDelete` 与禁用原因，不自行硬编码复杂规则。
- 后端在修改/删除接口必须二次校验上述规则，避免绕过前端直接调用。

### 8.4.1 社团详情
- `GET /api/school-admin/clubs/{clubId}/manage-detail`

Response:
```json
{
  "clubId": 283357707223040,
  "clubName": "晨曦音乐社",
  "category": "文化类",
  "status": 2,
  "applyStatus": 4,
  "initiatorRealName": "张三",
  "instructorName": "李老师",
  "purpose": "丰富校园文化活动",
  "charterUrl": "https://oss.example.com/club-apply/charter/20260311/abc.pdf",
  "instructorProofUrl": "https://oss.example.com/club-apply/instructor-proof/20260312/proof.zip",
  "createdAt": "2026-03-12T00:25:27.74"
}
```
说明：
- 该接口不返回申请阶段备注（`remark`），社团管理详情页无需展示该字段。

### 8.4.2 修改社团
- `PATCH /api/school-admin/clubs/{clubId}`

Request:
```json
{
  "clubName": "晨曦音乐社",
  "category": "文化类",
  "purpose": "丰富校园文化活动",
  "instructorName": "李老师"
}
```

建议规则：
- 审批流进行中（`applyStatus` 为 `1/2/3`）禁止修改
- 已注销（`status=5`）禁止修改
- 修改社团信息仅修改社团基础信息，不修改申请阶段备注 `remark`。

### 8.4.3 删除社团
- `DELETE /api/school-admin/clubs/{clubId}`

建议规则：
- 仅允许删除“驳回且待审核”或“已注销”记录
- 后端需进行二次校验，禁止前端绕过

### 8.4.4 冻结/解冻社团状态
- `PATCH /api/school-admin/clubs/{clubId}/status`

Request（冻结）：
```json
{
  "status": "FROZEN",
  "reason": "学校管理员手动冻结社团"
}
```

Request（解冻）：
```json
{
  "status": "ACTIVE",
  "reason": "学校管理员手动解冻社团"
}
```

状态映射建议：
- `FROZEN` -> `club.status = 3`（限期整改/冻结）
- `ACTIVE` -> `club.status = 2`（正常）

前端按钮约定：
- 当 `club.status = 2` 时展示“冻结”
- 当 `club.status = 3` 时展示“解冻”
- 仅 `status` 在 `2/3` 时允许切换，其他状态按钮禁用并提示原因

日志限制：
- 社团冻结/解冻**可以写入 `audit_log`**。

### 8.8 统计总览
- `GET /api/school-admin/statistics/overview`

Response:
```json
{
  "activeClubCount": 98,
  "totalClubCount": 120,
  "activeUserCount": 13045,
  "totalUserCount": 15420,
  "pendingApprovalCount": 17,
  "suspiciousExpenseCount": 6
}
```

字段说明：
- `activeClubCount`: 正常社团数
- `totalClubCount`: 全部社团数
- `activeUserCount`: 活跃用户数（仅非冻结用户）
- `totalUserCount`: 全部用户数（冻结 + 非冻结）

### 8.9 定时任务状态查询（用于按钮刷新后状态保持）
- `GET /api/school-admin/scheduled-tasks/status?taskCodes=GRADUATION_EXIT_CLUB,CLUB_CANCEL_FREEZE_ACCOUNT`

Response:
```json
[
  {
    "taskCode": "GRADUATION_EXIT_CLUB",
    "taskStatus": "STOPPED",
    "updatedAt": "2026-03-11T09:00:00+08:00"
  },
  {
    "taskCode": "CLUB_CANCEL_FREEZE_ACCOUNT",
    "taskStatus": "STARTED",
    "updatedAt": "2026-03-10T18:10:20+08:00"
  }
]
```

`taskStatus` 寤鸿鍊硷細
- `STOPPED`锛堟寜閽伆鑹诧級
- `STARTED` / `RUNNING`锛堟寜閽豢鑹诧級

### 8.10 瀹氭椂浠诲姟鍚姩/鍏抽棴鍛戒护
- `POST /api/school-admin/scheduled-tasks/{taskCode}/command`

`taskCode` 寤鸿锛?
- `GRADUATION_EXIT_CLUB`
- `CLUB_CANCEL_FREEZE_ACCOUNT`

Request锛堝惎鍔級锛?
```json
{
  "action": "START",
  "reason": "瀛︽牎绠＄悊鍛樻墜鍔ㄥ惎鍔?
}
```

Request锛堝叧闂級锛?
```json
{
  "action": "STOP",
  "reason": "瀛︽牎绠＄悊鍛樻墜鍔ㄥ叧闂?
}
```

Response:
```json
{
  "taskCode": "GRADUATION_EXIT_CLUB",
  "action": "START",
  "taskStatus": "STARTED",
  "updatedAt": "2026-03-11T16:20:35+08:00"
}
```


### 8.11 社团申报详情查询（审批列表“社团详情”按钮）
- `GET /api/school-admin/approvals/clubs/{approvalId}`

Response:
```json
{
  "id": 283357707223041,
  "clubId": 283357707223040,
  "clubName": "晨曦音乐社",
  "category": "文化类",
  "applyStatus": 1,
  "initiatorRealName": "张三",
  "instructorName": "李老师",
  "purpose": "丰富校园文化活动",
  "remark": "首批发起人20人",
  "charterUrl": "https://oss.example.com/club-apply/charter/20260311/abc.pdf",
  "instructorProofUrl": "https://oss.example.com/club-apply/instructor-proof/20260312/proof.zip",
  "createdAt": "2026-03-12T00:25:27.74"
}
```
## 9. 鍚庣瀹炵幇瑕佹眰锛堝繀椤伙級
- 鎵€鏈夌鐞嗗憳鎺ュ彛闇€瑕佷簩娆￠壌鏉冿細瑙掕壊鏍￠獙 + 鏁版嵁鑼冨洿鏍￠獙
- 绀惧洟绠＄悊鍛樻帴鍙ｅ繀椤婚檺鍒跺湪 `club-admin/me` 浣滅敤鍩?
- 鐢ㄦ埛鐘舵€佹帶鍒跺繀椤诲湪缃戝叧鎴栦笟鍔″眰缁熶竴鎷︽埅锛歚ACTIVE` 涔嬪鐘舵€佹嫆缁濊闂彈淇濇姢璧勬簮
- 鈥滄瘯涓氳嚜鍔ㄩ€€绀锯€濃€滅ぞ鍥㈡敞閿€鍐荤粨璐﹀彿鈥濋渶鏀寔瀹氭椂浠诲姟涓庢墜鍔ㄨЕ鍙戜袱绉嶆柟寮?
- 鍏抽敭鎿嶄綔蹇呴』璁板綍鎿嶄綔瀛楁锛歚operatorId/operatorRole/targetId/before/after/ip/ua/createdAt`
- 璇存槑锛?
闈炲鎵圭被鎿嶄綔锛堝鐢ㄦ埛鐘舵€佸垏鎹€佸畾鏃朵换鍔″惎鍋溿€侀〉闈㈡搷浣滄棩蹇楋級涓嶅緱鍐欏叆 `audit_log`锛屽簲鍐欏叆鐙珛涓氬姟鏃ュ織琛ㄦ垨鎵╁睍鏃ュ織浣撶郴銆?
- 瀵瑰杩斿洖绂佹娉勯湶鏁忔劅瀛楁锛堝瀵嗙爜鍝堝笇銆佸唴閮ㄩ鎺ц瘎鍒嗭級

## 10. 鍓嶅悗绔仈璋冩竻鍗?
- 鐧诲綍鎴愬姛鍚?`permissionType` 涓庡墠绔鑹叉槧灏勪竴鑷?
- `/auth/me` 杩斿洖 `status`锛岀敤浜庡墠绔处鍙风姸鎬佹嫤鎴?
- 鎵€鏈夊垪琛ㄦ帴鍙ｈ嚦灏戞敮鎸?`pageNum/pageSize`
- 涓氬姟閿欒閫氳繃 `code/message` 杩斿洖鍙淇℃伅
- 娑夊強骞跺彂鎵ｅ噺鐨勬姤鍚嶆帴鍙ｉ渶瑕佸帇娴嬮獙璇佹棤瓒呭崠

## 11. 社团注销流程接口（新增）

### 11.1 文件上传（资产清算报告）
- `POST /api/files/upload`

FormData 字段：
- `file`：`multipartFile`
- `bizType`：`CLUB_CANCEL_ASSET_SETTLEMENT`

Response:
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "url": "https://oss.example.com/club-cancel/asset-settlement/20260312/settlement.pdf"
  }
}
```

说明：
- 该上传接口与社团申报文件上传共用，`bizType` 新增 `CLUB_CANCEL_ASSET_SETTLEMENT`。

### 11.2 社团管理员提交注销申请
- `POST /api/club-admin/me/club/cancel`

Request:
```json
{
  "applyType": 1,
  "applyReason": "连续两学期无活动，申请注销",
  "assetSettlementUrl": "https://oss.example.com/club-cancel/asset-settlement/20260312/settlement.pdf"
}
```

Response `data`:
```json
{
  "id": 90001,
  "clubId": 283357707223040,
  "applyType": 1,
  "applyReason": "连续两学期无活动，申请注销",
  "assetSettlementUrl": "https://oss.example.com/club-cancel/asset-settlement/20260312/settlement.pdf",
  "cancelStatus": 1,
  "createdAt": "2026-03-12T16:20:35+08:00"
}
```

### 11.3 社团管理员查询我的注销进度
- `GET /api/club-admin/me/club/cancel-applies?pageNum=1&pageSize=20`

Response `data`:
```json
{
  "records": [
    {
      "id": 90001,
      "clubId": 283357707223040,
      "clubName": "晨曦音乐社",
      "applyType": 1,
      "applyReason": "连续两学期无活动，申请注销",
      "assetSettlementUrl": "https://oss.example.com/club-cancel/asset-settlement/20260312/settlement.pdf",
      "cancelStatus": 2,
      "createdAt": "2026-03-12T16:20:35+08:00"
    }
  ],
  "total": 1,
  "pageNum": 1,
  "pageSize": 20
}
```

### 11.4 学校管理员注销审核列表
- `GET /api/school-admin/approvals/club-cancels?pageNum=1&pageSize=10&keyword=&cancelStatus=`

### 11.5 学校管理员注销审核详情
- `GET /api/school-admin/approvals/club-cancels/{cancelId}`

### 11.6 学校管理员推进/驳回注销审核
- `PATCH /api/school-admin/approvals/club-cancels/{cancelId}/status`

推进请求示例：
```json
{
  "cancelStatus": 2,
  "opinion": "学校管理员推进社团注销审核流程"
}
```

驳回请求示例：
```json
{
  "cancelStatus": 5,
  "opinion": "学校管理员驳回社团注销申请"
}
```

### 11.7 注销状态与流转规则
- `cancelStatus` 定义：
  - `1` 待公示
  - `2` 待经费结清
  - `3` 待资产移交
  - `4` 已完成
  - `5` 驳回
- 只允许：`1 -> 2 -> 3 -> 4`（逐步推进，禁止跳步）
- 任意非终态（`1/2/3`）可驳回到 `5`
- 终态 `4/5` 不允许再变更




