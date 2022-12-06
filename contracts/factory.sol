// SPDX-License-Identifier:MIT
// Adjust your own solc
pragma solidity ^0.8.14;

import "@openzeppelin/contracts/token/ERC1155/ERC1155.sol";
import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "hardhat/console.sol";

library IterableMapping {
    // Iterable mapping from address to uint;


    struct pointInfo{
        string name;
        address lord;
        address conaddress;
        string imageposition;
    }

    struct Map {
        string[] keys;
        string[] namelist;
        string[] urllist;
        mapping(string => pointInfo) values; // 每个经纬度 指定一个lord的地址 
        mapping(string => uint) indexOf;   // 
        mapping(string => bool) inserted;
    }

    function get(Map storage map, string memory key) public view returns (pointInfo memory) {
        return map.values[key];
    }

    function getKeyAtIndex(Map storage map, uint index) public view returns (string memory) {
        return map.keys[index];
    }

    function size(Map storage map) public view returns (uint) {
        return map.keys.length;
    }

    function set(
        Map storage map,
        string memory key,
        pointInfo memory val
    ) public {
        if (map.inserted[key]) {
            map.values[key] = val;
        } else {
            map.inserted[key] = true;
            map.values[key] = val;
            map.indexOf[key] = map.keys.length;
            map.keys.push(key);
            map.namelist.push(val.name);
            map.urllist.push(val.imageposition);
        }
    }

    function remove(Map storage map, string memory key) public {
        if (!map.inserted[key]) {
            return;
        }

        delete map.inserted[key];
        delete map.values[key];

        uint index = map.indexOf[key];
        uint lastIndex = map.keys.length - 1;
        string memory lastKey = map.keys[lastIndex];

        map.indexOf[lastKey] = index;
        delete map.indexOf[key];

        map.keys[index] = lastKey;
        map.keys.pop();
    }
}


//每一个地图只有一个LandMark 跟point绑定
//消耗AP 产生合约 
contract LandMark is ERC1155{
    address public bank;
    address public owner;

    constructor (address _owner, uint256 amount ,string memory url ) ERC1155(""){
        bank = msg.sender;
        owner = _owner;


        _mint(_owner,random(10000), amount,bytes(url));

    }


    function random(uint num) public view returns(uint){
        return uint(keccak256(abi.encodePacked(block.timestamp,block.difficulty, 
        msg.sender))) % num;
    }

    
    function MintPieces(address to,uint256 id, uint256 amount) external returns (bool) {

        // address to,
        // uint256 id,
        // uint256 amount,
        // bytes memory data
        _mint(to, id, amount,"");
        return false;
    }
}


contract LandMarkFactory {

    using IterableMapping for IterableMapping.Map;
    using IterableMapping for IterableMapping.pointInfo;

    IterableMapping.Map private map;
    LandMark[] public LandMarkMarket;

    address _APToken;

    uint256 _initPrice = 10;

    constructor(address _owner, address aptoken) {
        _APToken = aptoken;
        _owner = msg.sender;
    }


    function getLalo() external view returns(string[] memory,string[] memory,string[] memory) {

        return (map.keys,map.namelist,map.urllist);
    }
    // 创建LandMark contract
    function createLandMark(string memory lalo,string memory name  ,uint256 pieceamount ,string memory url) external payable returns(address,uint) {

        // IERC20(_APToken).approve(address(this), _initPrice);
        // IERC20(_APToken).transferFrom(msg.sender,address(this),_initPrice);
        //消耗ap点进行创建合约        
        require(map.inserted[lalo] == false, "current point has already been created Land");        
        console.log(
            "point 1"
        );
        LandMark LandMarkInstance = new LandMark(msg.sender,pieceamount,url );
        console.log(
            "point 2"
        );
        address LandMarkAddress = address(LandMarkInstance);
        console.log(
            "point 3"
        );
        //这里是不是应该用map?
        //map 第一额参数是他自己，这里就不需要再次输入，有点像golang中的struct 自有函数?
        map.set(lalo, IterableMapping.pointInfo({
            name: name,
            lord: msg.sender,
            conaddress: LandMarkAddress,
            imageposition: url
        }));
        console.log(
            "point 4"
        );
        // LandMarkMarket.push(LandMarkInstance);

        return (LandMarkAddress,uint(0));
    }
}