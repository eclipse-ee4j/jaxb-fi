#!/bin/csh
#
# DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
#
# Copyright (c) 2012, 2023 Oracle and/or its affiliates. All rights reserved.
#
# Oracle licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#



saxtosaxevent $1 > /dev/null
# check for an error
if ($? == 1) then
  echo $1 ERROR does not parse with Xerces
  rm $1
else
	echo : PASSED $1
fi
