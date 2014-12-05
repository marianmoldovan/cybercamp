from gcm import GCM

gcm = GCM('AIzaSyCxXaknhqHcNAxxSKGseYQrpgHB5COLF00')
data = {'type': 'value1', 'message': 'value2'}
try:
	gcm.plaintext_request(registration_id='APA91bGxJ3Y2n4pExPc8kX1PAyuARTuA9p8a3LxwTj9d6LbAQ3aE4TYeaJ13nxqDohOtBW0ief8VdQl_H4Zz31gm5Csa61eT68RyeudpJH7lwJrzy-5yl3VmvZzYU3uIOnYMTfSGMozmhkV6DfEGblz6xUGmLrYBxQ', data=data, retries=10 )
except:
	print 'fail'
