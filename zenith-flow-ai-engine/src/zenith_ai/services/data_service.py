import baostock as bs
from datetime import datetime, timedelta
import logging

logger = logging.getLogger(__name__)

class DataService:
    @staticmethod
    def get_index_stocks(index_code: str) -> list[str]:
        stocks = []
        try:
            lg = bs.login()
            if lg.error_code != '0':
                logger.error(f"Baostock login failed: {lg.error_msg}")
                return []
            
            date = datetime.now().strftime("%Y-%m-%d")
            rs = None
            # Simple retry logic
            for i in range(5):
                check_date = (datetime.now() - timedelta(days=i)).strftime("%Y-%m-%d")
                
                if '000300' in index_code:
                    rs = bs.query_hs300_stocks(date=check_date)
                elif '000905' in index_code: 
                    rs = bs.query_zz500_stocks(date=check_date)
                elif '000016' in index_code:
                    rs = bs.query_sz50_stocks(date=check_date)
                else:
                    rs = bs.query_hs300_stocks(date=check_date)
                
                if rs and rs.error_code == '0' and rs.next():
                    # If we found data, break the retry loop
                    # We need to query again because calling next() consumes a row
                    if '000300' in index_code:
                        rs = bs.query_hs300_stocks(date=check_date)
                    elif '000905' in index_code: 
                        rs = bs.query_zz500_stocks(date=check_date)
                    elif '000016' in index_code:
                        rs = bs.query_sz50_stocks(date=check_date)
                    else:
                        rs = bs.query_hs300_stocks(date=check_date)
                    break
            
            if rs and rs.error_code == '0':
                while rs.next():
                    row = rs.get_row_data()
                    if len(row) > 1:
                        full_code = row[1]
                        if '.' in full_code:
                            code = full_code.split('.')[1]
                            stocks.append(code)
            
            bs.logout()
            return stocks
        except Exception as e:
            logger.error(f"Error fetching index stocks: {e}")
            return []

