USE [up7]
GO
/****** 对象:  StoredProcedure [dbo].[f_process]    脚本日期: 10/30/2012 15:20:07 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		Xproer
-- Create date: 2012-10-25
-- Description:	更新文件进度
-- =============================================
CREATE PROCEDURE [dbo].[f_process]
	-- Add the parameters for the stored procedure here
	@f_pos bigint
	,@f_uid int
	,@f_id int
	,@f_lenSvr bigint
	,@f_perSvr nvarchar(6)
	,@f_complete bit

AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    DECLARE @sql nvarchar(4000);
	DECLARE @ParamDef nvarchar(500);

    SET @sql=N'update up7_files set f_pos=@f_pos,f_lenSvr=@f_lenSvr,f_perSvr=@f_perSvr,f_complete=@f_complete where f_uid=@f_uid and f_id=@f_id'
	SET @ParamDef = N'@f_pos bigint
					,@f_uid int
					,@f_id int
					,@f_lenSvr bigint
					,@f_perSvr nvarchar(6)
					,@f_complete bit';

    -- Insert statements for procedure here
	EXEC sp_executesql @sql,@ParamDef,@f_pos,@f_uid,@f_id,@f_lenSvr,@f_perSvr,@f_complete
END