import unittest
import json
import requests
import gps
import get_questions

class GPSTest(unittest.TestCase):
    def test_gps_type(self):
        gps_type = update_gps.get_gps()
		self.assertEqual(tup, type(gps_type)) #Should be in type tuple initially
		gps_str = update_gps.convert_tup_to_str(gps_type)
		self.assertEqual(str, type(gps_str)) #Should be in type string

if __name__ == '__main__':
    unittest.main()
			
