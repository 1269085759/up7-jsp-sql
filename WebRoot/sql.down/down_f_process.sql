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
--drop procedure [dbo].[down_f_process]
CREATE PROCEDURE [dbo].[down_f_process]
	-- Add the parameters for the stored procedure here
	 @f_lenLoc bigint
	,@f_perLoc nvarchar(6)
	,@f_uid int
	,@f_id int

AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    DECLARE @sql nvarchar(4000);
	DECLARE @ParamDef nvarchar(500);

    SET @sql=N'update down3_files set f_lenLoc=@f_lenLoc,f_perLoc=@f_perLoc where f_uid=@f_uid and f_id=@f_id'
	SET @ParamDef = N'@f_lenLoc bigint
					,@f_perLoc nvarchar(6)
					,@f_uid bit
					,@f_id int';

    -- Insert statements for procedure here
	EXEC sp_executesql @sql,@ParamDef,@f_lenLoc,@f_perLoc,@f_uid,@f_id
END

