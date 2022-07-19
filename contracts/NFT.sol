//SPDX-License-Identifier: Unlicense
pragma solidity ^0.8.1;
import "@openzeppelin/contracts/access/AccessControl.sol";
import "@openzeppelin/contracts/access/Ownable.sol";
import "@openzeppelin/contracts/token/ERC1155/ERC1155.sol";

contract NFT is AccessControl, Ownable, ERC1155{
    bytes32 public constant OWNER_ROLE = keccak256("OWNER_ROLE");
    bytes32 public constant ADMIN_ROLE = keccak256("ADMIN_ROLE");

    uint256 public constant startpoint = 1000000;
    uint256 public currenttokenid = startpoint;
    //for list token => url
    mapping(uint256 => string) public tokenmapping ;

    //address => token tuple
    struct tokenIDvsPosition {
        uint256 tokendindex;
        string  urlstring;
    }
    mapping(address => tokenIDvsPosition[]) public addresslist;
    uint256 public constant Start = 1;
    //构造函数
    constructor(
        address admin
    ) ERC1155("https//ipfs.io/ipfs/xxxxxxxxxxxxxx/{id}.json"){


        _setupRole(OWNER_ROLE, msg.sender);
        _setRoleAdmin(OWNER_ROLE, OWNER_ROLE);
        _setRoleAdmin(ADMIN_ROLE, OWNER_ROLE);
        _setupRole(ADMIN_ROLE, admin);
    }

    //used for add admin control 
    modifier onlyOwnerAndAdmin() { // Modifier
        require(
            hasRole(ADMIN_ROLE, msg.sender)|| (owner() == msg.sender),
            "Only owner and admin can call this."
        );
        _;
    }

    function transferOwnership(address newOwner) public override onlyOwner{

        super.transferOwnership(newOwner);

        _setupRole(OWNER_ROLE, newOwner);

        //revoke at last
        revokeRole(OWNER_ROLE,msg.sender);

    }
    function supportsInterface(bytes4 interfaceId) public view virtual override(ERC1155, AccessControl) returns (bool) {
        return super.supportsInterface(interfaceId);
    }

    // function mintnft(address user,string memory uristring) public{
    //     _mint(user,currenttokenid,1, bytes(""));
    //     tokenmapping[currenttokenid] = string(uristring);

    //     tokenIDvsPosition memory tp ;
    //     tp.tokendindex = currenttokenid;
    //     tp.urlstring = uristring;

    //     // tmptokenlist[currenttokenid] = string(uristring);
    //     addresslist[user].push(tp);
    //     currenttokenid = currenttokenid + 1;
    // }

    function mintnft(uint256 tokenid, address user,string memory uristring) public{
        _mint(user,tokenid,1, bytes(""));
        tokenmapping[tokenid] = string(uristring);

        tokenIDvsPosition memory tp ;
        tp.tokendindex = tokenid;
        tp.urlstring = uristring;

        // tmptokenlist[currenttokenid] = string(uristring);
        addresslist[user].push(tp);

    }


    function uri(uint256 _tokenid) override public pure returns (string memory) {
        return string(
            abi.encodePacked(
                "https://ipfs.io/ipfs/bafybeihjjkwdrxxjnuwevlqtqmh3iegcadc32sio4wmo7bv2gbf34qs34a/",
                Strings.toString(_tokenid),".json"
            )
        );
    }

    function transfernft(address to, uint256 id,uint256 amount) public onlyOwnerAndAdmin{
        safeTransferFrom(msg.sender,to,id,amount,"");
    }

    function tokenlistbyaddress(address user) public view returns (tokenIDvsPosition[] memory){
        return addresslist[user];
    }


}